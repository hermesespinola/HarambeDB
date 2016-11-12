package database.table;

import java.util.Arrays;
import structures.dict.Dict;
import structures.dict.LinkedDict;
import structures.list.List;
import structures.list.ArrayLinearList;
import structures.tree.AVL;
import structures.node.KeyValueNode;
import database.table.relation.Relation;
import database.HarambException;
import database.Database;
import database.table.column.*;
import database.table.row.*;
import java.util.ArrayList;
import java.io.File;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

// TODO: check partitions avl when removing a row
public class Table<PrimaryKey extends Comparable<? super PrimaryKey>> implements Serializable {
  // avl tree containing the location and first value of the diferent partitions of the table
  private AVL<PrimaryKey, Integer> partitions;

  // the name of the primary key column
  // String pkColName; TODO: add this later

  // path to the table file
  private final String path;

  // number of partitions in the table
  private int partitionCount;

  // Dict of columns and their indexes
  private ColumnList columns;

  // partition of the table, dictionary of rows
  private transient Partition<PrimaryKey> currentPartition;
  private final Class<?> primaryKeyType;

  // maximum number of entries before partitioning the table
  private transient static final int THRESHOLD = 50;
  private static final long serialVersionUID = 05L;
  public transient static final String extension = ".hbtb";
  String tableName;

  public Table(String dbPath, String tableName, Class<?> primaryKeyType) throws HarambException {
    this.path = dbPath + tableName + '/';
    this.tableName = tableName;
    this.primaryKeyType = primaryKeyType;
    File tableDir = new File(this.path);
    if (!tableDir.exists()) {
      try {
        tableDir.mkdir();
        ObjectOutputStream oos = new ObjectOutputStream(
          new FileOutputStream(this.path + tableName + extension));
        oos.writeObject(this);
        oos.close();
      } catch (Exception e) {
        throw new HarambException(e);
      }
    } else {
      // TODO: hacer más cosas
      throw new HarambException("Table " + tableName + " already exists");
    }
    partitions = new AVL<>();
    currentPartition = new HarambePartition<PrimaryKey>(this.path, partitionCount);
    this.columns = new HarambeColumnList();
  }

  public Class<?> getPrimaryKeyType() {
    return this.primaryKeyType;
  }

  public Column addColumn(String name, Class<?> type) {
    Column newCol = new Column(columns.size(), type);
    columns.add(name, newCol);
    return newCol;
  }

  // cut lesser values of current table and paste it in the next table with lesserKey (left child)
  private void dividePartition() throws HarambException {
    List<PrimaryKey> keys = currentPartition.getKeys();
    Partition<PrimaryKey> newPartition = new HarambePartition<>(this.path, partitionCount);

    // move half of the bigger values in current partition to the new partition...
    for (int i = keys.size()/2; i < keys.size(); i++) {
      newPartition.addRow(keys.get(i), currentPartition.getRow(keys.get(i)));
      currentPartition.removeRow(keys.get(i));
    }
    partitions.add(newPartition.getKeys().get(0), partitionCount++);
    newPartition.save();
  }

  public void removeRow(PrimaryKey key) throws HarambException {
    loadPartition(key);
    // if the smallest key is removed update the avl tree
    if (currentPartition.removeRow(key)) {
      Integer ptNumber = partitions.get(key);
      partitions.remove(key);
      partitions.add(key, ptNumber);
    }
  }

  // returns the added row
  public Row addRow(PrimaryKey key) throws HarambException {
    if (partitions.isEmpty()) {
      partitions.add(key, partitionCount++);
      currentPartition.addRow(key, new HarambeRow(this.columns));
      return currentPartition.getRow(key);
    }

    loadPartition(key);
    if (key.compareTo(currentPartition.getKeys().get(0)) < 0) {
      partitions.remove(currentPartition.getKeys().get(0));
      partitions.add(key, currentPartition.partitionNumber());
    }

    currentPartition.addRow(key, new HarambeRow(this.columns));
    if (currentPartition.size() > THRESHOLD) {
      dividePartition();
    }
    return this.getRow(key);
  }

  public Row getRow(PrimaryKey key) throws HarambException {
    loadPartition(key);
    Row row = currentPartition.getRow(key);
    if (row == null) {
      throw new HarambException("No such row: " + key);
    }
    return row;
  }

  private <OtherPrimaryKey extends Comparable<? super OtherPrimaryKey>> void getRowWithRelationsUtil(PrimaryKey key, Database db, ArrayLinearList<Row> target) throws HarambException {
    loadPartition(key);
    int idx = target.size();  // index of the row of the current table
    target.add(getRow(key));
    for (Column col : columns) {
      if (col.hasRelation()) {
        if (col.relationType() == Relation.Type.oneToOne) {
          col.getRelatedTable(db).getRowWithRelationsUtil(target.get(idx).get(col), db, target);
        } else {
          Table<OtherPrimaryKey> related = col.getRelatedTable(db);
          OtherPrimaryKey[] keys = target.get(idx).get(col);
          for (OtherPrimaryKey r : keys) {
            related.getRowWithRelationsUtil(r, db, target);
          }
        }
      }
    }
  }

  public <OtherPrimaryKey extends Comparable<? super OtherPrimaryKey>> List<Row> getRowWithRelations(PrimaryKey key, Database db) throws HarambException {
    ArrayLinearList<Row> rows = new ArrayLinearList<>();
    this.getRowWithRelationsUtil(key, db, rows);
    return rows;
  }

  private final void loadPartition(PrimaryKey keyInRange) throws HarambException {
    KeyValueNode<PrimaryKey,Integer> partitionInfo = partitions.getClosest(keyInRange);
    if (currentPartition == null) {
      currentPartition = Partition.load(this.path, partitionInfo.getValue());
    } else if (partitionInfo.getKey().compareTo(currentPartition.getKeys().get(0)) != 0) {
      currentPartition.save(); // TODO: save only if data changed
      currentPartition = Partition.load(this.path, partitionInfo.getValue());
    }
  }

  public Column getColumn(String columnName) {
    return this.columns.get(columnName);
  }

  public void save() throws HarambException {
    currentPartition.save();
    try (ObjectOutputStream oos = new ObjectOutputStream(
    new FileOutputStream(this.path + tableName + extension))) {
      oos.writeObject(this);
    } catch (Exception e) {
      throw new HarambException(e);
    }
  }

  public String name() {
    return this.tableName;
  }

  @SuppressWarnings("unchecked")
  public static final <T extends Comparable<? super T>> Table<T> load(final String dbPath, final String tableName) throws HarambException {
    try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
      new FileInputStream(dbPath + tableName + '/' + tableName + extension)))) {
        Table<T> table = (Table<T>) ois.readObject();
        table.currentPartition = Partition.load(table.path, 0);
        return table;
    } catch (Exception e) {
      throw new HarambException(e);
    }
  }
  //
  // public static final void showMenu() {
  //   System.out.println("1) Create table");
  //   System.out.println("2) Load table");
  //   System.out.println("3) Save table");
  //   System.out.println("4) Add column");
  //   System.out.println("5) Add row");
  //   System.out.print(": ");
  // }
}
