package database.table;

import database.table.relation.Relation;
import structures.list.ArrayLinearList;
import structures.node.KeyValueNode;
import java.io.BufferedInputStream;
import structures.dict.LinkedDict;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import database.HarambException;
import java.io.FileOutputStream;
import database.table.column.*;
import java.io.FileInputStream;
import java.io.Serializable;
import database.table.row.*;
import structures.dict.Dict;
import structures.list.List;
import java.util.ArrayList;
import structures.tree.AVL;
import database.Database;
import java.util.Arrays;
import java.io.File;

/**
* This Table Class, along with Partition are the Backbone of HarambeDB, it
* creates a directory inside the project where the table and partition files
* are stored, with extensions .hbtb and .hbpt respectibly, each partition has an
* auto-increment ID to identify the partition uniquely.
*
* An AVL tree is used to index the database, the key of the tree node
* is the minimum value in that particular partition and the value is the ID of
* the partition. The minimum value is used to determine in which partition a
* certain row should be stored or retrieved from.
*
* The THRESHOLD constant is the maximum number of elements a partition can store
* before another partition is automatically created, feel free to modify this
* value as you need.
*
* <p>This class is a member of the
* <a href="{@docRoot}/index.html" target="_top">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
* @see     Partition
* @see     HarambePartition
* @see     ColumnList
* @see     Column
* @see     Row
*/
public class Table<PrimaryKey extends Comparable<? super PrimaryKey>> implements Serializable {
  /**
  * avl tree containing the ID and minimum value of the diferent partitions of the table
  */
  private AVL<PrimaryKey, Integer> partitions;

  /**
  * Path to the table directory
  */
  private final String path;

  /**
  * Number of current partitions in the table
  */
  private int partitionCount;

  /**
  * Dict of columns and their indexes
  */
  private ColumnList columns;

  /**
  * Partition of the table, dictionary of rows
  */
  private transient Partition<PrimaryKey> currentPartition;

  /**
  * The class of the primaryKeyType
  */
  private final Class<?> primaryKeyType;

  /**
  * Maximum number of entries before partitioning the table
  */
  private transient static final int THRESHOLD = 50;
  private static final long serialVersionUID = 05L;

  /**
  * The extension of the table file
  */
  public transient static final String extension = ".hbtb";

  /**
  * Name of the table and the PrimaryKey Column
  */
  String tableName;

  /**
  * Name of the primary key column
  */
  String pkName;

  /**
  * Creates a new table within the specified database directory
  * @param  dbPath          The path to the database directory this table belongs to
  * @param  tableName       The name of the tablle
  * @param  primaryKeyType  The Class of the primary key
  * @param  primaryKeyName  The name of the primary key column
  * @throws HarambException If there is an error creating the table file or if a database with the same name already exists
  */
  public Table(String dbPath, String tableName, Class<?> primaryKeyType, String primaryKeyName) throws HarambException {
    this.pkName = primaryKeyName;
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
      throw new HarambException("Table " + tableName + " already exists");
    }
    partitions = new AVL<>();
    currentPartition = new HarambePartition<PrimaryKey>(this.path, partitionCount);
    this.columns = new HarambeColumnList();
  }

  /**
  * {@link Table#primaryKeyType}
  * @return The data type of the primary key
  */
  public Class<?> getPrimaryKeyType() {
    return this.primaryKeyType;
  }

  /**
  * Adds a new column to the table
  * @param  name  The name of the new column
  * @param  type  The data type that the column will store
  * @return       The new column
  */
  public Column addColumn(String name, Class<?> type) {
    Column newCol = new Column(columns.size(), type);
    columns.add(name, newCol);
    return newCol;
  }

  /**
  * Cuts a sorted partition in half, creates a new partition with it and pushes
  * it to the AVL partition tree
  * @throws HarambException If there is an error reading or writing partition files
  */
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

  /**
  * Adds a row to the table, it returns the new row so you can chain set calls
  * @param  key               The value of the primary key of the row
  * @throws HarambException   If there is an error reading a partition file
  * @return The added row
  */
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

  /**
  * Retrieves a single row in the table
  * @param  key             The value of the primary key of the row
  * @throws HarambException If there is an error reading a partition file or there is no row with the specified primary key
  * @return                 The row with primary key equals to key
  */
  public Row getRow(PrimaryKey key) throws HarambException {
    loadPartition(key);
    Row row = currentPartition.getRow(key);
    if (row == null) {
      throw new HarambException("No such row: " + key);
    }
    return row;
  }

  /**
  * Retrieves the row of the specified key and its chain of relations (i.e.:
  * if the related table of one row has a column with another relation then the
  * row of that relation is retrieved as well)
  * @param  key               The value of the primary key of the row
  * @param  db                The database the related tables are located
  * @param  <OtherPrimaryKey> The data type of the primary key of the other table
  * @throws HarambException   If there is an error reading table or partition files
  * @return                   A list of rows containing the row of this table at position 0 and related rows from 1 up to n, where n is the size of the list.
  */
  public <OtherPrimaryKey extends Comparable<? super OtherPrimaryKey>> ArrayLinearList<Row> getRowWithRelation(PrimaryKey key, Database db) throws HarambException {
    loadPartition(key);
    ArrayLinearList<Row> rows = new ArrayLinearList<>(5);
    rows.add(getRow(key));
    for (Column col : columns) {
      if (col.hasRelation()) {
        if (col.relationType() == Relation.Type.oneToOne) {
          rows.add(col.getRelatedTable(db).getRow(rows.get(0).get(col)));
        } else {
          // it is guaranteed that every field in the related column holds an array.
          Table<OtherPrimaryKey> related = col.getRelatedTable(db);
          OtherPrimaryKey[] keys = rows.get(0).get(col);
          for (OtherPrimaryKey r : keys) {
            rows.add(related.getRow(r));
          }
        }
      }
    }
    return rows;
  }

  /**
  * Retrieves the row of the specified key and its chain of relations (i.e.:
  * if the related table of one row has a column with another relation then the
  * row of that relation is retrieved as well)
  * @param  key               The value of the primary key of the row
  * @param  db                The database the related tables are located
  * @param  target            The list of rows where the rows should be added recursively
  * @throws HarambException   If there is an error reading table or partition files
  */
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

  /**
  * Retrieves the row of the specified key and its chain of relations (i.e.:
  * if the related table of one row has a column with another relation then the
  * row of that relation is retrieved as well)
  * @param  key               The value of the primary key of the row
  * @param  db                The database the related tables are located
  * @param  <OtherPrimaryKey> The data type of the primary key of the other table
  * @throws HarambException   If there is an error reading table or partition files
  * @return                   A list of size n where list[0] is the row of this column and list[1:n] is the chain of relations
  */
  public <OtherPrimaryKey extends Comparable<? super OtherPrimaryKey>> List<Row> getRowWithRelations(PrimaryKey key, Database db) throws HarambException {
    ArrayLinearList<Row> rows = new ArrayLinearList<>(10);
    this.getRowWithRelationsUtil(key, db, rows);
    return rows;
  }

  /**
  * Closes the current partition and saves it if necessary, then loads the
  * partition where ketInRange should be
  * @param  keyInRange        A value that helps to find the partition to load
  * @throws HarambException   If there is an error reading or saving a partition file
  */
  private final void loadPartition(PrimaryKey keyInRange) throws HarambException {
    KeyValueNode<PrimaryKey,Integer> partitionInfo = partitions.getClosest(keyInRange);
    if (currentPartition == null) {
      currentPartition = Partition.load(this.path, partitionInfo.getValue());
    } else if (partitionInfo.getKey().compareTo(currentPartition.getKeys().get(0)) != 0) {
      currentPartition.save();
      currentPartition = Partition.load(this.path, partitionInfo.getValue());
    }
  }

  /**
  * {@link ColumnList#get}
  * @param  columnName  The name of the column to get
  * @return             The column with the name specified
  */
  public Column getColumn(String columnName) {
    return this.columns.get(columnName);
  }

  /**
  * Saves the actual state of the table in its corresponding .hbtb file
  * @throws HarambException If there is an IOException
  */
  public void save() throws HarambException {
    currentPartition.save();
    try (ObjectOutputStream oos = new ObjectOutputStream(
    new FileOutputStream(this.path + tableName + extension))) {
      oos.writeObject(this);
    } catch (Exception e) {
      throw new HarambException(e);
    }
  }

  /**
  * {@link Table#tableName}
  * @return The name of the table
  */
  public String name() {
    return this.tableName;
  }

  /**
  * {@link Table#pkName}
  * @return  The name of the primary key column
  */
  public String primaryKeyName() {
    return this.pkName;
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
}
