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

/**
* The main Partition implementation in HarambeDB, it stores a dictionary of primary keys
* mapping to rows. The Partition is stored in a .hbpt file inside a table directory.
*
* This implementation of the Partition interface stores a sorted list of the primary keys,
* it could also be rewriten to sort the keys every time they are required, as stated in
* the Partition documentation.
*
* <p>This class is a member of the
* <a href="{@docRoot}/index.html" target="_top">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
* @see     Table
* @see     Row
*/
public class HarambePartition<PrimaryKey extends Comparable<? super PrimaryKey>> implements Partition<PrimaryKey>, Serializable {

  /**
  * The partition ID
  */
  private int partitionNumber;

  /**
  * A list where keys are stored sorted
  */
  private DoublyLinkedList<PrimaryKey> sortedKeys;

  /**
  * A dictionary of primary keys mapping to rows
  */
  private Dict<PrimaryKey, Row> rows;

  /**
  * The path of to the partition file
  */
  private final String path;
  private static final long serialVersionUID = 15L;

  /**
  * Creates a new partition inside a table directory and with an ID, if the ID
  * already exists the other table will be overwritten.
  * @param  tablePath         The path to the table directory
  * @param  partitionNumber   The ID of the new partition
  */
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

  /**
  * Adds a key in the sorted array of keys
  * @param  key The key to add.
  */
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

  public void addRow(PrimaryKey key, Row row) throws HarambException {
    if (rows.getValue(key) != null)
      throw new HarambException("Key already exists: " + key);
    rows.add(key, row);
    this.addSortedKey(key);
  }

  public Row getRow(PrimaryKey key) {
    return rows.getValue(key);
  }

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
