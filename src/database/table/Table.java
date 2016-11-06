package database.table;

import structures.dict.Dict;
import structures.dict.LinkedDict;
import structures.list.List;
import structures.tree.AVL;
import structures.node.KeyValueNode;
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
import java.io.IOException;

public class Table<PrimaryKey extends Comparable<? super PrimaryKey>> implements Serializable {
  // avl tree containing the location and first value of the diferent partitions of the table
  private AVL<PrimaryKey, Integer> partitions;

  // path to the table file
  private final String path;

  // number of partitions in the table
  private int partitionCount;

  // Dict of columns and their indexes
  private ColumnList columns;

  // partition of the table, dictionary of rows
  private transient Partition<PrimaryKey> currentPartition;

  // maximum number of entries before partitioning the table
  private transient static final int THRESHOLD = 1;
  private static final long serialVersionUID = 05L;
  public transient static final String extension = ".hbtb";

  public Table(String tableName) throws IOException {
    // TODO: change to some other directory
    File tableDir = new File("../" + tableName);
    if (!tableDir.exists()) {
      try {
        tableDir.mkdir();
        ObjectOutputStream oos = new ObjectOutputStream(
          new FileOutputStream("../" + tableName + '/' + tableName + extension));
        oos.writeObject(this);
        oos.close();
      } catch (SecurityException se) {
        se.printStackTrace();
      }
    } else {
      // TODO: hacer más cosas
      throw new IOException("Table already exists"); // TODO: change to HarambException
    }
    this.path = "../" + tableName + '/';
    partitions = new AVL<>();
    currentPartition = new HarambePartition<PrimaryKey>(this.path, partitionCount);
    this.columns = new HarambeColumnList();
  }

  public <ColumnDataType> Table<PrimaryKey> addColumn(String name, Class<ColumnDataType> type) {
    columns.add(name, new Column(columns.size(), type));
    return this;
  }

  // cut lesser values of current table and paste it in the next table with lesserKey (left child)
  private void dividePartition() throws IOException {
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

  public void removeRow(PrimaryKey key) {
    loadPartition(key);
    currentPartition.removeRow(key);
  }

  public void addRow(PrimaryKey key) throws IOException {
    if (partitions.isEmpty()) {
      partitions.add(key, partitionCount++);
      currentPartition.addRow(key, new HarambeRow(this.columns));
      return;
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
  }

  public Row getRow(PrimaryKey key) throws Exception {
    loadPartition(key);
    Row row = currentPartition.getRow(key);
    if (row == null) {
      throw new Exception("No such row: " + key);
    }
    return row;
  }

  private final void loadPartition(PrimaryKey keyInRange) {
    KeyValueNode<PrimaryKey,Integer> partitionInfo = partitions.getClosest(keyInRange);
    if (partitionInfo.getKey().compareTo(currentPartition.getKeys().get(0)) != 0) {
      try {
        currentPartition.save();
      } catch (IOException e) {
        e.printStackTrace();
      }
      currentPartition = Partition.load(this.path, partitionInfo.getValue());
    }
  }

  public Column getColumn(String columnName) {
    return this.columns.get(columnName);
  }

  @SuppressWarnings("unchecked")
  public static final <T extends Comparable<? super T>> Table<T> load(String tableName) {
    try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
      new FileInputStream("../" + tableName + '/' + tableName + extension)))) {
        return (Table<T>) ois.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
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


  public static void main(String[] args) throws Exception {

    // Table<String> users = Table.load("Users");
    Table<String> users = new Table<String>("Users");

    users.addColumn("Address", String.class);
    users.addColumn("Age", Integer.class);

    users.addRow("Chuck Norris");
    users.getRow("Chuck Norris").set(users.getColumn("Address"), "Dirección de Chuck")
    .set(users.getColumn("Age"), 1001);

    users.addRow("Lucio");
    users.getRow("Lucio").set(users.getColumn("Address"), "Dirección de Lucio")
      .set(users.getColumn("Age"), 19);

    users.addRow("Manolo");
    users.getRow("Manolo").set(users.getColumn("Address"), "Dirección de manolo");
    users.getRow("Manolo").set(users.getColumn("Age"), 123);

    System.out.println(users.getRow("Chuck Norris"));
    System.out.println(users.getRow("Lucio"));
    System.out.println(users.getRow("Manolo"));
  }
}
