package database;

import structures.dict.Dict;
import structures.dict.LinkedDict;
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

@SuppressWarnings("rawtypes")
public class Database implements Serializable {
  private static final long serialVersionUID = 14L;
  public transient static final String extension = ".hbdb";

  // protected DirectedGraph<Column> relationships;
  protected final String path;

  public Database(String dbName) throws HarambException {
    this.path = "../" + dbName + '/';

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
    return new Table<T>(this.path, tableName);
  }

  @SuppressWarnings("unchecked")
  private <T extends Comparable<? super T>> Table<T> getTable(String tableName) throws HarambException {
    return (Table<T>) Table.load(this.path + tableName + '/', tableName);
  }

  public void dropTable(String tableName) throws IOException {
    Path dirPath = Paths.get( this.path + tableName );
    Files.walk(dirPath).map( Path::toFile )
      .sorted( Comparator.comparing( File::isDirectory ) )
      .forEach( File::delete );
  }

  public static final Database load(final String dbName) throws HarambException {
    try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
      new FileInputStream("../" + dbName + '/' + dbName + extension)))) {
        return (Database) ois.readObject();
    } catch (Exception e) {
      throw new HarambException(e);
    }
  }

  public static void main(String[] args) throws Exception {
    Database db = Database.load("Expenses");
    // db.dropTable("Users");
    Table<String> users = db.getTable("Users");
    // Table<String> users = db.createTable("Users", String.class);
    // users.addColumn("Address", String.class);
    // users.addRow("Lucio");
    // users.getRow("Lucio").set(users.getColumn("Address"), "Lucio's Address");
    // users.save();
    System.out.println(users.getRow("Lucio"));
  }
}
