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

import database.table.relation.RelationType;
import structures.list.List;
import database.table.row.Row;
import database.table.column.Column;
import java.util.Arrays;

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
    try {
      Table<T> t = new Table<T>(this.path, tableName, primaryKeyType);
      tableMap.add(tableName, tables.size());
      tables.add(t);
      return t;
    } catch (HarambException he) {
      saveDbObject();
      throw he;
    }
  }

  @SuppressWarnings("unchecked")
  public <T extends Comparable<? super T>> Table<T> getTable(String tableName) throws HarambException {
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
    saveDbObject();

  }

  public static final Database load(final String dbName) throws HarambException {
    try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
      new FileInputStream("../" + dbName + '/' + dbName + extension)))) {
        Database db = (Database) ois.readObject();
        int size = db.tableMap.getSize();
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

  // TODO: add primaryKey Column name to Table
  public static void main(String[] args) throws Exception {
    // Database db = new Database("Expenses");
    //
    // Table<String> users = db.createTable("Users", String.class); // Name
    // users.addColumn("Address", String.class);
    // users.addColumn("Invoices", Integer[].class);
    // users.addRow("Lucio").set(users.getColumn("Address"), "Lucio's Address").set(users.getColumn("Invoices"), new Integer[] {1, 2, 3});
    // users.addRow("Hermes").set(users.getColumn("Address"), "Hermes's Address").set(users.getColumn("Invoices"), new Integer[] {4, 6, 8});
    // users.addRow("Eros").set(users.getColumn("Address"), "Eros's Address").set(users.getColumn("Invoices"), new Integer[] {5, 7, 9});

    // Table<Integer> invoices = db.createTable("Invoices", Integer.class); // Invoice Number
    // Column paymentColumn = invoices.addColumn("Payment", Integer.class);
    // Column itemsColumn = invoices.addColumn("Items", String[].class);
    // invoices.addRow(1).set(paymentColumn, 500).set(itemsColumn, new String[] {"Beer", "Food"});
    // invoices.addRow(2).set(paymentColumn, 860).set(itemsColumn, new String[] {"Meat", "Beer"});
    // invoices.addRow(3).set(paymentColumn, 345).set(itemsColumn, new String[] {"Beer", "Maionaise"});
    // invoices.addRow(4).set(paymentColumn, 765).set(itemsColumn, new String[] {"Water", "Egg", "Apple Juice", "Whore"});
    // invoices.addRow(5).set(paymentColumn, 234).set(itemsColumn, new String[] {"Food", "Egg"});
    // invoices.addRow(6).set(paymentColumn, 646).set(itemsColumn, new String[] {"Water", "Food", "Beer"});
    // invoices.addRow(7).set(paymentColumn, 123).set(itemsColumn, new String[] {"Meat", "Maionaise"});
    // invoices.addRow(8).set(paymentColumn, 435).set(itemsColumn, new String[] {"Food", "Whore"});
    // invoices.addRow(9).set(paymentColumn, 344).set(itemsColumn, new String[] {"Apple Juice", "Whore"});

    // Table<String> items = db.createTable("Items", String.class);
    // Column expenseColumn = items.addColumn("Expense", Integer.class);
    // items.addRow("Beer").set(expenseColumn, 100);
    // items.addRow("Food").set(expenseColumn, 120);
    // items.addRow("Meat").set(expenseColumn, 150);
    // items.addRow("Maionaise").set(expenseColumn, 40);
    // items.addRow("Water").set(expenseColumn, 87);
    // items.addRow("Egg").set(expenseColumn, 15);
    // items.addRow("Whore").set(expenseColumn, 300);
    // items.addRow("Apple Juice").set(expenseColumn, 145);

    // users.getColumn("Invoices").createRelation(invoices, RelationType.oneToMany);
    // invoices.getColumn("Items").createRelation(items, RelationType.oneToMany);
    // db.save();

    Database db = Database.load("Expenses");

    // List<Row> invoicesAndItems = invoices.getRowWithRelation(4, db);
    // for (Row row : invoicesAndItems) {
    //   for (Object val : row) {
    //     if (val.getClass().isArray()) {
    //       System.out.print(Arrays.toString((Object[])val) + " ");
    //     } else {
    //       System.out.print(val + " ");
    //     }
    //   }
    //   System.out.println();
    // }
  }
}
