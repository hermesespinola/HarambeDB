package hdb;

import structures.graph.unweighted.directed.AdjacencyList;
import hdb.table.relation.Relation;
import structures.list.ArrayLinearList;
import hdb.table.column.Column;
import java.io.BufferedInputStream;
import java.io.ObjectOutputStream;
import structures.dict.LinkedDict;
import java.io.ObjectInputStream;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import hdb.table.row.Row;
import java.io.BufferedReader;
import java.util.Collections;
import hdb.table.Table;
import java.io.Serializable;
import structures.list.List;
import structures.dict.Dict;
import java.util.Comparator;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.io.File;

/**
* This Database Class is the main Class in HarambeDB, it stores tables and
* organizes the structure of the database directory. The extensions of the
* database file is .hbdb, the database is stored in the project root folder,
* this folder contains the database file as well as the table directories, where
* a table file and partition files are stored.
*
* A directed unweighted adjacency list graph is used to store and look for
* relations. The adjacency list graph is used instead of a matrix implementation
* because a new node is added to the graph every time a new table is added to
* the hdb.
*
* The rootDir static constant is used to keep track of HarambeDB files on your system,
* You can change this to wherever you want to store your database files.
* If you have already created databases and you want to change this value make
* sure to move all files in the old directory to the new directory.
*
* Make sure you call the save method everytime you are done working with the database.
*
* <p>This class is a member of the
* <a href="{@docRoot}/index.html" target="_top">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
* @see     Relation
* @see     Table
*/
@SuppressWarnings("rawtypes")
public class Database implements Serializable {
  /**
  * The extension of the database file
  */
  public transient static final String extension = ".hbdb";

  /**
  * An array of tables in the database
  */
  protected transient ArrayLinearList<Table<?>> tables;

  /**
  * A dictionary that maps the name of the database to an index in the tables array
  */
  protected Dict<String, Integer> tableMap; // tableName -> table index in tables

  /**
  * The graph containing the relations
  */
  protected AdjacencyList relations;

  /**
  * The root directory for HarambeDB, where databases will be stored
  */
  public transient static final String rootDir = "../";

  /**
  * The local path to the database directory
  */
  protected final String path;

  /**
  * The name of the database
  */
  protected final String dbName;
  private static final long serialVersionUID = 14L;

  /**
  * Creates a new database with the given name
  * @param  dbName            The name of the new database
  * @throws HarambException   If the database already exists or if there is an error writing the database object
  */
  public Database(String dbName) throws HarambException {
    this.path = rootDir + dbName + '/';
    this.dbName = dbName;

    File dbDir = new File(this.path);
    if (!dbDir.exists()) {
      try {
        dbDir.mkdir();
        ObjectOutputStream oos = new ObjectOutputStream(
          new FileOutputStream(this.path + dbName + extension));
        oos.writeObject(this);
        oos.close();
      } catch (Exception e) {
        throw new HarambException(e);
      }
    } else {
      throw new HarambException("Database " + dbName + " already exists");
    }

    tableMap = new LinkedDict<>();
    tables = new ArrayLinearList<>();
    relations = new AdjacencyList(0);

    createSaveHook();
  }

  private void createSaveHook() {
    Database self = this;
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        self.save();
      }
    });
  }

  /**
  * Creates a new table in the database, if there is an error, it saves the
  * database and then throws the HarambException
  * @param  tableName           The name of the table to create
  * @param  primaryKeyType      The Class of the primary key of the new table
  * @param  primaryKeyName      The name of the primary key column in the new table
  * @param  <T>                 The data type of the primary key of the new table
  * @throws HarambException     If there is an error creating the new Table
  * @return                     The new table
  */
  public <T extends Comparable<? super T>> Table<T> createTable(String tableName, Class<T> primaryKeyType, String primaryKeyName) throws HarambException {
    try {
      Table<T> t = new Table<T>(this.path, tableName, primaryKeyType, primaryKeyName);
      tableMap.add(tableName, tables.size());
      tables.add(t);
      return t;
    } catch (HarambException he) {
      saveDbObject();
      throw he;
    }
  }

  /**
  * Retrieves a table
  * @param  tableName         The name of the table to get
  * @param  type              The Class of the primary key column in the table
  * @param  <T>               The data type of the primary key of the table
  * @throws HarambException   If the primary key is not of type 'type'
  * @return                   The table with the name specified
  */
  @SuppressWarnings("unchecked")
  public <T extends Comparable<? super T>> Table<T> getTable(String tableName, Class<?> type) throws HarambException {
    Table t = tables.get(tableMap.getValue(tableName));
    if (type != t.getPrimaryKeyType()) {
      throw new HarambException("Primary key of table " + tableName + " is not of " + type);
    }
    return (Table<T>) t;
  }

  /**
  * Deletes the table object and removes recursevely the directory of the table
  * @param  tableName     The name of the table to drop
  * @throws IOException   If there is an error deleting the files
  */
  public void dropTable(String tableName) throws IOException {
    // erase table directory recursevely
    Path dirPath = Paths.get( this.path + tableName );
    Files.walk(dirPath).map( Path::toFile )
      .sorted( Comparator.comparing( File::isDirectory ) )
      .forEach( File::delete );

    // remove table from tables and tableMap
    tables.set(tableMap.getValue(tableName), null);
    tableMap.remove(tableName);
    saveDbObject();
  }

  /**
  * Creeates a new relation between a column in a table and another table. The
  * data types of the column and the other table should match as described in the
  * Relation documentation.
  * @param  fromTable         The name of the table containing the column
  * @param  toTable           The name of the table to be related
  * @param  fromTableColumn   The name of the column
  * @param  type              The type of relation
  * @throws HarambException   If the table, column or relation do not exist and if the column and table data types missmatch
  */
  public void createRelation(String fromTable, String toTable, String fromTableColumn, Relation.Type type) throws HarambException {
    int t1Idx = tableMap.getValue(fromTable), t2Idx = tableMap.getValue(toTable);
    Table<?> t1 = tables.get(t1Idx);
    Table<?> t2 = tables.get(t2Idx);
    if (t1 == null) {
      throw new HarambException("Table " + fromTable + " does not exist");
    }
    if (t2 == null) {
      throw new HarambException("Table " + toTable + " does not exist");
    }
    Column c = t1.getColumn(fromTableColumn);
    if (c == null) {
      throw new HarambException("Column " + fromTableColumn + " in table " + t1.name() + " does not exists");
    }

    relations.addEdge(t1Idx, t2Idx);
    t1.getColumn(fromTableColumn).createRelation(t2, type);
  }

  /**
  * Retrieves the table a column is related to
  * @param  fromTable         The name of the table where the column with the relation is stored
  * @param  fromTableColumn   The name of the column that is related to another table
  * @throws HarambException   If the table, column or relation do not exist
  * @return                   The table the column is related to
  */
  public Table<?> getRelation(String fromTable, String fromTableColumn) throws HarambException {
    int t1Idx = tableMap.getValue(fromTable);
    Table<?> t1 = tables.get(t1Idx);
    if (t1 == null) {
      throw new HarambException("Table " + fromTable + " does not exist");
    }
    Column c = t1.getColumn(fromTableColumn);
    if (c == null) {
      throw new HarambException("Column " + fromTableColumn + " in table " + t1.name() + " does not exists");
    }
    if (!c.hasRelation()) {
      throw new HarambException("Column " + fromTableColumn + "has no relation");
    }
    return t1.getColumn(fromTableColumn).getRelatedTable(this);
  }

  /**
  * Removes a previously created relation
  * @param  fromTable         The name of the table where the column with the relation is stored
  * @param  toTable           The related Table
  * @param  fromTableColumn   The name of the column that is related to another table
  * @throws HarambException   If the tables, column or relation do not exist
  */
  public void removeRelation(String fromTable, String toTable, String fromTableColumn) throws HarambException {
    int t1Idx = tableMap.getValue(fromTable), t2Idx = tableMap.getValue(toTable);
    Table<?> t1 = tables.get(t1Idx);
    Table<?> t2 = tables.get(t2Idx);
    if (t1 == null) {
      throw new HarambException("Table " + fromTable + " does not exist");
    }
    if (t2 == null) {
      throw new HarambException("Table " + toTable + " does not exist");
    }
    Column c = t1.getColumn(fromTableColumn);
    if (c == null) {
      throw new HarambException("Column " + fromTableColumn + " in table " + t1.name() + " does not exists");
    }

    if (!c.hasRelation()) {
      throw new HarambException("Column " + fromTableColumn + "has no relation to " + t2.name());
    }

    c.removeRelation();
    relations.removeEdge(t1Idx, t2Idx);
  }

  /**
  * Retrieves a database stored in the current project
  * @param  dbName            The name of the database to load
  * @throws HarambException   If there is an error reading HarambeDB files or if the database does not exist
  * @return                   The loaded database object
  */
  public static final Database load(final String dbName) throws HarambException {
    try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
      new FileInputStream(rootDir + dbName + '/' + dbName + extension)))) {
        Database db = (Database) ois.readObject();
        // table map is not being loaded
        int size = db.tableMap.getSize();
        db.tables = new ArrayLinearList<Table<?>>(size, size * 2 + 5);
        for (String tableName : db.tableMap.keys()) {
          db.tables.set(db.tableMap.getValue(tableName), Table.load(db.path, tableName));
        }
        db.createSaveHook();
        return db;
    } catch (Exception e) {
      throw new HarambException(e);
    }
  }

  /**
  * Saves the database file
  * @throws HarambException If there is an error writing a file
  */
  public void saveDbObject() throws HarambException {
    try (ObjectOutputStream oos = new ObjectOutputStream(
      new FileOutputStream(this.path + this.dbName + extension))) {
      oos.writeObject(this);
    } catch (Exception e) {
      throw new HarambException("Could not save database");
    }
  }

  /**
  * Saves the database file and all the table files, you should call this method
  * every time you stop using the db, otherwise you'll lose the newly created data
  * @throws HarambException If there is an error writing a file
  */
  public void save() throws HarambException {
    for (Table<?> table : tables) {
      table.save();
    }

    saveDbObject();
  }
}
