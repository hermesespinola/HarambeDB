package database;

import structures.dict.Dict;
import structures.dict.LinkedDict;
import structures.list.ArrayLinearList;
import java.util.Comparator;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import database.table.Table;
import database.table.relationship.Relationship;

@SuppressWarnings("rawtypes")
public class Database implements Serializable {
  private static final long serialVersionUID = 14L;
  public transient static final String extension = ".hbdb";
  public transient ArrayLinearList<Table<?>> tables;
  public Dict<String, Integer> tableMap; // tableName -> table index in tables

  // protected DirectedGraph<Column> relationships;
  protected final String path;
  protected final String dbName;

  public Database(String dbName) throws HarambException {
    this.path = "../" + dbName + '/';
    this.dbName = dbName;
    tableMap = new LinkedDict<>();
    tables = new ArrayLinearList<>();

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
      // TODO: hacer más cosas
      throw new HarambException("Database " + dbName + " already exists");
    }
  }

  public <T extends Comparable<? super T>> Table<T> createTable(String tableName, Class<T> primaryKeyType) throws HarambException {
    Table<T> t = new Table<T>(this.path, tableName);
    tableMap.add(tableName, tables.size());
    tables.add(t);
    return t;
  }

  @SuppressWarnings("unchecked")
  private <T extends Comparable<? super T>> Table<T> getTable(String tableName) throws HarambException {
    return (Table<T>) tables.get(tableMap.getValue(tableName));
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
  }

  public static final Database load(final String dbName) throws HarambException {
    try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
      new FileInputStream("../" + dbName + '/' + dbName + extension)))) {
        Database db = (Database) ois.readObject();
        db.tables = new ArrayLinearList<Table<?>>(db.tableMap.getSize());
        System.out.println(db.tableMap);
        System.out.println(db.tables);
        for (String tableName : db.tableMap.keys()) {
          db.tables.set(db.tableMap.getValue(tableName), Table.load(db.path, tableName));
        }
        return db;
    } catch (Exception e) {
      throw new HarambException(e);
    }
  }

  public void save() throws HarambException {
    for (Table<?> table : tables) {
      table.save();
    }

    try (ObjectOutputStream oos = new ObjectOutputStream(
      new FileOutputStream(this.path + this.dbName + extension))) {
      oos.writeObject(this);
    } catch (Exception e) {
      throw new HarambException("Could not save database");
    }

  }

  // public static final Relationship oneToOneRelationship(Table<?> origin, Table<?> destiny, Column originField) {
  // }

  public static void main(String[] args) throws Exception {
    // Database db = new Database("Expenses");
    // Table<String> users = db.createTable("Users", String.class);
    // users.addColumn("Address", String.class);
    // users.addRow("Lucio");
    // users.getRow("Lucio").set(users.getColumn("Address"), "Lucio's Address");
    // db.save();

    Database db = Database.load("Expenses");
    Table<String> users = db.getTable("Users");


    System.out.println(users.getRow("Lucio"));
  }
}
