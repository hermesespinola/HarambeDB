package hdb.table;

import structures.list.ArrayLinearList;
import structures.node.KeyValueNode;
import hdb.table.relation.Relation;
import java.io.BufferedInputStream;
import structures.dict.LinkedDict;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.io.FileInputStream;
import java.io.Serializable;
import structures.dict.Dict;
import structures.list.List;
import structures.tree.AVL;
import hdb.HarambException;
import java.util.ArrayList;
import hdb.table.column.*;
import java.util.Iterator;
import java.util.Arrays;
import hdb.table.row.*;
import java.io.File;
import hdb.Database;

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
public class Table<PrimaryKey extends Comparable<? super PrimaryKey>> implements Serializable, Iterable<Row> {
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
    if (partitions.isEmpty()) {
      throw new HarambException("Table " + tableName + " is empty");
    }
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
  * @param  Class             The class of the other primary key
  * @param  db                The database the related tables are located
  * @param  <OtherPrimaryKey> The data type of the primary key of the other table
  * @throws HarambException   If there is an error reading table or partition files
  * @return                   A list of rows containing the row of this table at position 0 and related rows from 1 up to n, where n is the size of the list.
  */
  @SuppressWarnings("unchecked")
  public <OtherPrimaryKey extends Comparable<? super OtherPrimaryKey>> ArrayLinearList<Row> getRowWithRelation(PrimaryKey key, Database db) throws HarambException {
    ArrayLinearList<Row> rows = new ArrayLinearList<>(5);
    rows.add(getRow(key));
    for (Column col : columns) {
      if (col.hasRelation())
        if (col.relationType() == Relation.Type.oneToOne)
          try {
            if (rows.get(0).get(col) != null)
              rows.add(col.getRelatedTable(db).getRow(rows.get(0).get(col)));
          } catch (HarambException he) {
            // If the endpoind of the relation does not exist, either it has been
            // erased or it never existed, anyway, set the field of the column to null
            rows.set(0, getRow(key).set(col, null));
          }
        else {
          // it is guaranteed that every field in the related column holds an array.
          Table<OtherPrimaryKey> related = col.getRelatedTable(db);
          OtherPrimaryKey[] keys = rows.get(0).get(col);
          ArrayLinearList<OtherPrimaryKey> nonexistentEndpoints = new ArrayLinearList<>();
          for (OtherPrimaryKey r : keys)
            try {
              if (r != null) {
                rows.add(related.getRow(r));
              }
            } catch (HarambException he) {
              // If the endpoind of the relation does not exist, either it has been
              // erased or it never existed, keep track of these values to remove
              // them afterwards
              nonexistentEndpoints.add(r);
            }

          // If there were nonexistentEndpoints then remove the bad keys >:c
          if (!nonexistentEndpoints.empty()) {
            int i = 0;
            for (OtherPrimaryKey k : keys) {
              if (nonexistentEndpoints.indexOf(k) != -1)
                keys[i] = null;
              i++;
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
    int idx = target.size();  // index of the row of the current table
    target.add(getRow(key));
    for (Column col : columns) {
      if (col.hasRelation()) {
        if (col.relationType() == Relation.Type.oneToOne) {
          try {
            col.getRelatedTable(db).getRowWithRelationsUtil(target.get(idx).get(col), db, target);
          } catch (HarambException he) {
            target.add(getRow(key).set(col, null));
          }
        } else {
          Table<OtherPrimaryKey> related = col.getRelatedTable(db);
          OtherPrimaryKey[] keys = target.get(idx).get(col);
          ArrayLinearList<OtherPrimaryKey> nonexistentEndpoints = new ArrayLinearList<>();
          for (OtherPrimaryKey r : keys) {
            try {
              related.getRowWithRelationsUtil(r, db, target);
            } catch (HarambException he) {
              nonexistentEndpoints.add(r);
            }
          }
          // If there were nonexistentEndpoints then remove the bad keys >:c
          if (!nonexistentEndpoints.empty()) {
            int i = 0;
            for (OtherPrimaryKey k : keys) {
              if (nonexistentEndpoints.indexOf(k) != -1)
                keys[i] = null;
              i++;
            }
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
    if (partitionInfo == null) {
      throw new HarambException("Table " + tableName + " is empty");
    }
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

  /**
  * Adds blank spaces to the end of a string builder
  * @param  sb      The string builder
  * @param  amount  The amount of blank spaces to add
  */
  private void appendBlankSpaces(StringBuilder sb, int amount) {
    for (int i = 0; i < amount; i++) {
      sb.append(' ');
    }
  }

  /**
  * Calculate the difference between the two elements and adds them in such a way
  * that they are aligned.
  */
  private void center(StringBuilder columns, StringBuilder row, String cF, String rF) {
    int diff = cF.length() - rF.length();
    if (diff == 0) {
      columns.append(' ').append(cF).append(' ');
      row.append(' ').append(rF).append(' ');
    } else if (diff < 0) {
      float half = ((float) Math.abs(diff)) / 2;
      row.append(' ').append(rF).append(' ');
      appendBlankSpaces(columns, 1 + (int)Math.ceil(half));
      columns.append(cF);
      appendBlankSpaces(columns, 1 + (int)Math.floor(half));
    } else {
      float half = ((float) diff) / 2;
      columns.append(' ').append(cF).append(' ');
      appendBlankSpaces(row, 1 + (int)Math.ceil(half));
      row.append(rF);
      appendBlankSpaces(row, 1 + (int)Math.floor(half));
    }
  }

  /**
  * Prints a relation obtained from this table, (i.e. with relations=getRowWithRelation()).
  * @param  key        The key of the row to print
  * @param  relations  The result of some method to obtain the relations of a row
  */
  public void printRelation(PrimaryKey key, List<Row> relations) {
    StringBuilder cols = new StringBuilder();
    StringBuilder row = new StringBuilder();

    center(cols, row, pkName, key.toString());

    List<String> colNames = columns.names();

    for (int i = 0; i < columns.size(); i++) {
      Object field = relations.get(0).get(columns.get(colNames.get(i)));
      String fieldString = (field.getClass().isArray()) ? Arrays.toString((Object[])field) : field.toString();
      center(cols, row, colNames.get(i), fieldString);
    }

    // print results
    System.out.println(cols);
    StringBuilder separator = new StringBuilder();
    for (int i = 0; i < cols.length(); i++) {
      separator.append('-');
    }
    System.out.println(separator);
    System.out.println(row);
    System.out.println(separator);

    int depth = 0;
    int previousRowSize = 0;
    for (int i = 1; i < relations.size(); i++) {
      Row r = relations.get(i);
      if (previousRowSize != r.size()) {
        if (relations.get(i + 1) != null && relations.get(i - 1).size() == relations.get(i + 1).size()) {
          System.out.println(separator);
          depth--;
        } else {
          depth++;
        }
        previousRowSize = r.size();
      }
      for (int j = 0; j < depth; j++) {
        System.out.print("\t");
      }
      relations.get(i).print();
    }
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

  public Iterator<Row> iterator() {
    return new TableIterator();
  }

  // TODO: write the table Iterator
  public class TableIterator implements Iterator<Row> {
    public Row next() {

    }

    public boolean hasNext() {

    }
  }
}
