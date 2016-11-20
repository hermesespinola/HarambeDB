package database.table;

import structures.list.DoublyLinkedList;
import java.io.ObjectOutputStream;
import structures.dict.LinkedDict;
import java.io.ObjectInputStream;
import database.HarambException;
import java.io.FileOutputStream;
import database.table.row.Row;
import structures.list.List;
import structures.dict.Dict;
import java.io.Serializable;
import java.io.IOException;

public class HarambePartition<PrimaryKey extends Comparable<? super PrimaryKey>> implements Partition<PrimaryKey>, Serializable {
  private int partitionNumber;
  private DoublyLinkedList<PrimaryKey> sortedKeys;
  private Dict<PrimaryKey, Row> rows;
  private final String path;
  private static final long serialVersionUID = 15L;

  public HarambePartition(String tablePath, int partitionNumber) {
    this.path = tablePath + "pt" + partitionNumber + extension;
    this.partitionNumber = partitionNumber;
    this.rows = new LinkedDict<PrimaryKey, Row>();
    this.sortedKeys = new DoublyLinkedList<>();
    // file is not in the avl tree and there is no file for this partition
    try (ObjectOutputStream oos = new ObjectOutputStream(
    new FileOutputStream(this.path))) {
      oos.writeObject(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String path() {
    return this.path;
  }

  public int size() {
    return this.rows.getSize();
  }

  // adds a key to the sortedKeys list
  private void addSortedKey(PrimaryKey key) {
    if (this.sortedKeys.size() == 0) {
      this.sortedKeys.add(key);
      return;
    }


    if (key.compareTo(this.sortedKeys.get(0)) < 0) {
      this.sortedKeys.add(0, key);
      return;
    }
    else if(key.compareTo(this.sortedKeys.get(this.sortedKeys.size() - 1)) > 0) {
      this.sortedKeys.add(key);
      return;
    }

    int lower = 0;
    int upper = this.sortedKeys.size() - 1;
    int mid;

    while (true) {
      mid = lower + (upper - lower) / 2;
      if (lower + 1 == upper) {
        this.sortedKeys.add(lower, key);
        return;
      }
      if (key.compareTo(this.sortedKeys.get(mid)) > 0) {
        lower = mid;
      }
      else if (key.compareTo(this.sortedKeys.get(mid)) < 0) {
        upper = mid;
      }
    }
  }

  // save the partition in a .hbpt file
  public void save() throws HarambException {
    try (ObjectOutputStream oos = new ObjectOutputStream(
    new FileOutputStream(this.path))) {
      oos.writeObject(this);
    } catch (Exception e) {
      throw new HarambException(e);
    }
  }

  public List<PrimaryKey> getKeys() {
    return sortedKeys;
  }

  public int partitionNumber() {
    return this.partitionNumber;
  }

  public void addRow(PrimaryKey key, Row row) {
    rows.add(key, row);
    this.addSortedKey(key);
  }

  public Row getRow(PrimaryKey key) {
    return rows.getValue(key);
  }

  // returns true if the removed row's primary key is the smallest in the partitions.
  // returns false otherwise.
  public boolean removeRow(PrimaryKey key) throws HarambException {
    rows.remove(key);
    PrimaryKey firstKey = sortedKeys.get(0);
    PrimaryKey removedKey = sortedKeys.remove(sortedKeys.indexOf(key));
    if (key == null) {
      throw new HarambException("No such key");
    }
    return firstKey == removedKey;
  }

  public Dict<PrimaryKey, Row> rows() {
    return this.rows;
  }
}
