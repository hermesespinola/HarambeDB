package database;

import structures.graph.unweighted.directed.AdjacencyList;
import database.table.relation.Relation;
import structures.list.ArrayLinearList;
import database.table.column.Column;
import java.io.BufferedInputStream;
import java.io.ObjectOutputStream;
import structures.dict.LinkedDict;
import java.io.ObjectInputStream;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import database.table.row.Row;
import java.io.BufferedReader;
import java.util.Collections;
import database.table.Table;
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

@SuppressWarnings("rawtypes")
public class Database implements Serializable {
  private static final long serialVersionUID = 14L;
  public transient static final String extension = ".hbdb";
  public transient ArrayLinearList<Table<?>> tables;
  public Dict<String, Integer> tableMap; // tableName -> table index in tables

  protected AdjacencyList relations;
  protected final String path;
  protected final String dbName;

  public Database(String dbName) throws HarambException {
    this.path = "../" + dbName + '/';
    this.dbName = dbName;
    tableMap = new LinkedDict<>();
    tables = new ArrayLinearList<>();
    relations = new AdjacencyList(0);

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
  }

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

  @SuppressWarnings("unchecked")
  public <T extends Comparable<? super T>> Table<T> getTable(String tableName, Class<?> type) throws HarambException {
    Table t = tables.get(tableMap.getValue(tableName));
    if (type != t.getPrimaryKeyType()) {
      throw new HarambException("Primary key of table " + tableName + " is not of " + type);
    }
    return (Table<T>) t;
  }

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

  public Table<?> getRelation(String fromTable, String toTable, String fromTableColumn) throws HarambException {
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

    if (relations.getVertex(t1Idx).adjacentVertices().contains(relations.getVertex(t2Idx))) {
      return t1.getColumn(fromTableColumn).getRelatedTable(this);
    }
    return null;
  }

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

  public static final Database load(final String dbName) throws HarambException {
    try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
      new FileInputStream("../" + dbName + '/' + dbName + extension)))) {
        Database db = (Database) ois.readObject();
        int size = db.tableMap.getSize();
        // error here
        db.tables = new ArrayLinearList<Table<?>>(size, size * 2);
        for (String tableName : db.tableMap.keys()) {
          db.tables.set(db.tableMap.getValue(tableName), Table.load(db.path, tableName));
        }
        return db;
    } catch (Exception e) {
      throw new HarambException(e);
    }
  }

  private void saveDbObject() throws HarambException {
    try (ObjectOutputStream oos = new ObjectOutputStream(
      new FileOutputStream(this.path + this.dbName + extension))) {
      oos.writeObject(this);
    } catch (Exception e) {
      throw new HarambException("Could not save database");
    }
  }

  public void save() throws HarambException {
    for (Table<?> table : tables) {
      table.save();
    }

    saveDbObject();
  }
}
