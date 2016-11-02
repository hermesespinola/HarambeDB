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
  // number of segments
  private int partitionCount;
  // dictionary of columns and their data types
  private Dict<String, Class<?>> columns;
  // segment of the table, dictionary of rows
  private transient Dict<PrimaryKey, Dict<String, Object>> table;
  // maximum number of entries before segmenting the table
  private transient static final int THRESHOLD = 5;
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
        this.save();
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
  //
  private final void rebalancePartitions() {
    // FIXME: only works if table is LinkedDict.
    ArrayList<PrimaryKey> tableKeys = (ArrayList<PrimaryKey>) table.keys();
    Collections.sort(tableKeys);
    // create a new partition ...
    Dict<PrimaryKey, Dict<String, Object>> newTableSegment = new LinkedDict<>();
    // move half of the current partition to the new partition...
    for (int i = tableKeys.size()/2; i < tableKeys.size(); i++) {
      newTableSegment.add(tableKeys.get(i), table.getValue(tableKeys.get(i)));
    }
    createNewPartition(newTableSegment, tableKeys.get(0));
  }

  public Table<PrimaryKey> addRow(PrimaryKey key) {
    if (currentPartitionLesserKey == null) {
      currentPartitionLesserKey = key;
      if (tablePartitions.isEmpty()) {
        tablePartitions.add(key, currentPartitionPath);
      }
    } else if (key.compareTo(currentPartitionLesserKey) < 0) {
      if (tablePartitions.contains(currentPartitionLesserKey)) {
        tablePartitions.remove(currentPartitionLesserKey);
        tablePartitions.add(key, currentPartitionPath);
      }
      currentPartitionLesserKey = key;
    }

    loadPartition(key);
    if (table.getSize() == THRESHOLD) {
      rebalancePartitions();
      loadPartition(key);
    }
    table.add(key, new LinkedDict<String, Object>());
    return this;
  }

  public Dict<String, Object> getRow(PrimaryKey key) {
    loadPartition(key);
    Dict<String, Object> row = table.getValue(key);
    return row;
  }

  public Object getCell(PrimaryKey key, String column) {
    checkColumn(column);
    Dict<String, Object> row = getRow(key);
    return row.getValue(column);
  }

  public Class<?> getTypeofColumn(String columnName) {
    return columns.getValue(columnName);
  }

  public Table<PrimaryKey> addCell(PrimaryKey key, String column, Object value) {
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

  // saves segment to file and add it to the segments tree.
  private final void createNewPartition(Dict<PrimaryKey, Dict<String, Object>> segment, PrimaryKey lesserKey) {
    String path = "../" + tableName + '/' + tableName + partitionCount + ".hseg";
    try (ObjectOutputStream oos = new ObjectOutputStream(
    new FileOutputStream(path))) {
      oos.writeObject(segment);
      tablePartitions.add(lesserKey, path);
      tablePartitions.add(lesserKey, path);
      partitionCount++;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private final void saveCurrentPartition() {
    // TODO: Make this more elegant (?)
    if (currentPartitionLesserKey == null) {
      return;
    }
    if (currentPartitionPath == null) {
      // file is not in the avl tree and there is no file for this segment
      currentPartitionPath = "../" + tableName + '/' + tableName + partitionCount + ".hseg";
      try (ObjectOutputStream oos = new ObjectOutputStream(
      new FileOutputStream(currentPartitionPath))) {
        oos.writeObject(table);
        tablePartitions.add(currentPartitionLesserKey, currentPartitionPath);
        partitionCount++;
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      // file is already in the avl tree and there is already a file for this segment
      try (ObjectOutputStream oos = new ObjectOutputStream(
      new FileOutputStream(currentPartitionPath))) {
        oos.writeObject(table);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @SuppressWarnings("unchecked")
  private final Dict<PrimaryKey, Dict<String, Object>> loadPartition(PrimaryKey keyInRange) {
    KeyValueNode<PrimaryKey,String> partitionInfo = tablePartitions.getClosest(keyInRange);
    if (partitionInfo.getKey().compareTo(currentPartitionLesserKey) != 0) {
      if (currentPartitionPath != partitionInfo.getValue()) {
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
        new FileInputStream(partitionInfo.getValue())))) {
          saveCurrentPartition();
          currentPartitionPath = partitionInfo.getValue();
          currentPartitionLesserKey = partitionInfo.getKey();
          return (Dict<PrimaryKey, Dict<String, Object>>) ois.readObject();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return null;
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


  public static void main(String[] args) throws IOException {
    Table<String> users = new Table<String>("Users");

    users.addColumn("Address", String.class);

    users.addRow("Manolo");
    users.addRow("Lucio");
    users.addRow("Teletubi");
    users.addRow("Miguel");
    users.addRow("Miguelito");
    users.addRow("Chuck");
    // System.out.println(users.table.keys());
  }
}
