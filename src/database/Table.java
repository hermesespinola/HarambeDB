import structures.dict.Dict;
import structures.dict.LinkedDict;
import structures.tree.AVL;
import structures.node.KeyValueNode;
import java.util.ArrayList;
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

public class Table<PrimaryKey extends Comparable<? super PrimaryKey>> implements Serializable {
  // avl tree containing the location and first value of the diferent partitions of the table
  private AVL<PrimaryKey, String> tablePartitions;
  // name of the table
  private String tableName;
  // number of partitions
  private int partitionCount;
  // dictionary of columns and their data types
  private Dict<String, Class<?>> columns;
  // partition of the table, dictionary of rows
  private transient Dict<PrimaryKey, Dict<String, Object>> table;
  // maximum number of entries before partitioning the table
  private transient static final int THRESHOLD = 1;
  private transient String currentPartitionPath;
  private transient PrimaryKey currentPartitionLesserKey;
  private static final long serialVersionUID = 05L;
  public transient static final String extension = ".hbtb";

  public Table(String tableName) throws IOException {
    table = new LinkedDict<PrimaryKey, Dict<String, Object>>();
    columns = new LinkedDict<String, Class<?>>();
    tablePartitions = new AVL<PrimaryKey, String>();
    this.tableName = tableName;
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
      throw new IOException("Table already exists");
    }
  }

  public Table<PrimaryKey> addColumn(String name, Class<?> type) {
    columns.add(name, type);
    return this;
  }

  // cut lesser values of current table and paste it in the next table with lesserKey (left child)
  private final void rebalancePartitions() {
    // FIXME: only works if table is LinkedDict (modify OpenAddressingDict).
    ArrayList<PrimaryKey> tableKeys = (ArrayList<PrimaryKey>) table.keys(); // TODO: Partition.getPrimaryKeys();
    Collections.sort(tableKeys);
    // create a new partition ...
    Dict<PrimaryKey, Dict<String, Object>> newPartition = new LinkedDict<>(); // TODO: Partition
    // move half of the current partition to the new partition...
    for (int i = tableKeys.size()/2; i < tableKeys.size(); i++) {
      newPartition.add(tableKeys.get(i), table.getValue(tableKeys.get(i))); // TODO: Partition.addRow
      table.remove(tableKeys.get(i)); // TODO: Partition.removeRow
    }
    createNewPartition(newPartition, tableKeys.get(tableKeys.size()/2)); // TODO new Partition
  }

  // TODO: Partition.removeRow
  public Table<PrimaryKey> removeRow(PrimaryKey key) {
    loadPartition(key);
    table.remove(key); // TODO: Partition.removeRow;
    return this;
  }

  // TODO: Row.add
  public Table<PrimaryKey> removeCell(PrimaryKey key, String column) throws Exception {
    Dict<String, Object> row = getRow(key);
    row.remove(column);
    return this;
  }

  // TODO: Partition.addRow;
  public Table<PrimaryKey> addRow(PrimaryKey key) {
    if (tablePartitions.isEmpty()) {
      currentPartitionLesserKey = key;
      currentPartitionPath = "../" + tableName + '/' + tableName + partitionCount + ".hbsg";
      saveCurrentPartition();
    } else if (key.compareTo(currentPartitionLesserKey) < 0) {
      tablePartitions.remove(currentPartitionLesserKey);
      tablePartitions.add(key, currentPartitionPath);
      currentPartitionLesserKey = key;
    }

    loadPartition(key); // TODO: Partition.load
    table.add(key, new LinkedDict<String, Object>()); // TODO: Partition.addRow;
    if (table.getSize() > THRESHOLD) {
      rebalancePartitions();
    }
    return this;
  }

  // TODO: Partition.getRow;
  public Dict<String, Object> getRow(PrimaryKey key) throws Exception {
    loadPartition(key);
    Dict<String, Object> row = table.getValue(key);
    if (row == null) {
      throw new Exception("No such row");
    }
    return row;
  }

  public Object getCell(PrimaryKey key, String column) throws Exception {
    checkColumn(column);
    Dict<String, Object> row = getRow(key);
    return row.getValue(column);
  }

  public Class<?> getTypeofColumn(String columnName) {
    return columns.getValue(columnName);
  }

  public Table<PrimaryKey> addCell(PrimaryKey key, String column, Object value) throws Exception {
    checkColumn(column);
    checkColumnType(column, value);
    Dict<String, Object> row = getRow(key);
    row.add(column, value);
    return this;
  }

  private final void checkColumn(final String column) {
    if (columns.getValue(column) == null) {
      throw new RuntimeException("No such Column"); // TODO: create a HarambException c:
    }
  }

  private final void checkColumnType(final String column, final Object value) {
    if (columns.getValue(column) != value.getClass()) {
      throw new RuntimeException("Column datatype missmatch");
    }
  }

  // saves partition to file and add it to the parition tree.
  private final void createNewPartition(Dict<PrimaryKey, Dict<String, Object>> pt, PrimaryKey lesserKey) {
    String path = "../" + tableName + '/' + tableName + ++partitionCount + ".hbsg";
    try (ObjectOutputStream oos = new ObjectOutputStream(
    new FileOutputStream(path))) {
      oos.writeObject(pt);
      tablePartitions.add(lesserKey, path);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private final void saveCurrentPartition() {
    if (currentPartitionLesserKey == null) {
      return;
    }
    if (!tablePartitions.contains(currentPartitionLesserKey)) {
      // file is not in the avl tree and there is no file for this partition
      try (ObjectOutputStream oos = new ObjectOutputStream(
      new FileOutputStream(currentPartitionPath))) {
        oos.writeObject(table);
        tablePartitions.add(currentPartitionLesserKey, currentPartitionPath);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      // file is already in the avl tree and there is already a file for this partition
      try (ObjectOutputStream oos = new ObjectOutputStream(
      new FileOutputStream(currentPartitionPath))) {
        oos.writeObject(table);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @SuppressWarnings("unchecked")
  private final void loadPartition(PrimaryKey keyInRange) {
    KeyValueNode<PrimaryKey,String> partitionInfo = tablePartitions.getClosest(keyInRange);
    if (partitionInfo.getKey().compareTo(currentPartitionLesserKey) != 0) {
      try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
      new FileInputStream(partitionInfo.getValue())))) {
        saveCurrentPartition();
        currentPartitionPath = partitionInfo.getValue();
        currentPartitionLesserKey = partitionInfo.getKey();
        table = (Dict<PrimaryKey, Dict<String, Object>>) ois.readObject();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private final void save() {
    try (ObjectOutputStream oos = new ObjectOutputStream(
      new FileOutputStream("../" + tableName + '/' + tableName + extension))) {
      saveCurrentPartition();
      oos.writeObject(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static final Table<?> load(String tableName) {
    try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
      new FileInputStream("../" + tableName + '/' + tableName + extension)))) {
        return (Table) ois.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static final void showMenu() {
    System.out.println("1) Create table");
    System.out.println("2) Load table");
    System.out.println("3) Save table");
    System.out.println("4) Add column");
    System.out.println("5) Add row");
    System.out.print(": ");
  }


  public static void main(String[] args) throws Exception {
    // try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
    // new FileInputStream("../Users/Users1.hbsg")))) {
    //   System.out.println((Dict<String, Dict<String, Object>>) ois.readObject());
    // } catch (Exception e) {
    //   e.printStackTrace();
    // }

    Table<String> users = new Table<String>("Users");

    users.addColumn("Address", String.class);

    users.addRow("Manolo");
    users.addCell("Manolo", "Address", "Manolo address");
    users.addRow("Lucio");
    users.addCell("Lucio", "Address", "Lucio address");
    users.addRow("Teletubi");
    users.addCell("Teletubi", "Address", "Teletubi address");
    users.addRow("Miguel");
    users.addCell("Miguel", "Address", "Miguel address");
    users.addRow("Miguelito");
    users.addCell("Miguelito", "Address", "Miguelito address");
    users.addRow("Chuck");
    users.addCell("Chuck", "Address", "Chuck address");

    System.out.println(users.getRow("Teletubi"));
    System.out.println(users.getRow("Chuck"));
    System.out.println(users.getRow("Miguelito"));
    System.out.println(users.getRow("Lucio"));
    System.out.println(users.getRow("Miguel"));
    System.out.println(users.getRow("Manolo"));

    users.removeCell("Lucio", "Address");
    System.out.println(users.getRow("Lucio"));
  }
}
