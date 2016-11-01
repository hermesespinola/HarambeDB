import structures.dict.Dict;
import structures.dict.LinkedDict;
import structures.tree.AVL;
import structures.node.KeyValueNode;
import java.io.File;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

public class Table<PrimaryKey extends Comparable<? super PrimaryKey>> implements Serializable {
  // avl tree containing the location and first value of the diferent segments of the table
  private AVL<PrimaryKey, String> tableSegments;
  // The path to the segment file
  private String currentSegmentPath;
  // name of the table
  private String tableName;
  // number of segments
  private int segmentCount;
  // dictionary of columns and their data types
  private Dict<String, Class<?>> columns;
  // dictionary of rows
  private transient Dict<PrimaryKey, Dict<String, Object>> table;
  // maximum number of entries before segmenting the table
  private transient static final int THRESHOLD = 5;
  private PrimaryKey currentSegmentLesserKey;
  private static final long serialVersionUID = 05L;
  public static final String extension = ".htab";

  public Table(String tableName) {
    table = new LinkedDict<PrimaryKey, Dict<String, Object>>();
    columns = new LinkedDict<String, Class<?>>();
    tableSegments = new AVL<PrimaryKey, String>();
    this.tableName = tableName;
    File tableDir = new File("../" + tableName);
    if (!tableDir.exists()) {
      try {
        tableDir.mkdir();
      } catch (SecurityException se) {
        se.printStackTrace();
      }
    }
  }

  public Table<PrimaryKey> addColumn(String name, Class<?> type) {
    columns.add(name, type);
    return this;
  }

  public Table<PrimaryKey> addRow(PrimaryKey key) {
    if (tableSegments.isEmpty()) {
      table.add(key, new LinkedDict<String, Object>());
    } else {
      // segment where data should go
      KeyValueNode<PrimaryKey,String> segmentNode = tableSegments.getClosest(key);
      if (segmentNode.getKey().compareTo(currentSegmentLesserKey) != 0) {
        loadSegment(segmentNode.getValue(), segmentNode.getKey());
      }
      table.add(key, new LinkedDict<String, Object>());
    }

    if (currentSegmentLesserKey == null) {
      currentSegmentLesserKey = key;
    } else if (key.compareTo(currentSegmentLesserKey) < 0) {
      if (tableSegments.contains(currentSegmentLesserKey)) {
        tableSegments.remove(currentSegmentLesserKey);
        tableSegments.add(key, currentSegmentPath);
      }
      currentSegmentLesserKey = key;
    }
    return this;
  }

  public Dict<String, Object> getRow(PrimaryKey key) {
    Dict<String, Object> row = table.getValue(key);
    if (row == null) {
      throw new RuntimeException("No such row");
    }
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
      throw new RuntimeException("No such Column");
    }
  }

  private final void checkColumnType(final String column, final Object value) {
    if (columns.getValue(column) != value.getClass()) {
      throw new RuntimeException("Column datatype missmatch");
    }
  }

  private final void saveCurrentSegment() {
    if (currentSegmentPath == null) {
      // file is not in the avl tree and there is no file for this segment
      currentSegmentPath = "../" + tableName + '/' + tableName + segmentCount + ".hseg";
      try (ObjectOutputStream oos = new ObjectOutputStream(
      new FileOutputStream(currentSegmentPath))) {
        oos.writeObject(table);
        tableSegments.add(currentSegmentLesserKey, currentSegmentPath);
        segmentCount++;
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      // file is already in the avl tree and there is already a file for this segment
      try (ObjectOutputStream oos = new ObjectOutputStream(
      new FileOutputStream(currentSegmentPath))) {
        oos.writeObject(table);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @SuppressWarnings("unchecked")
  private final Dict<PrimaryKey, Dict<String, Object>> loadSegment(String path, PrimaryKey lesserKey) {
    if (currentSegmentPath != path) {
      try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
      new FileInputStream(path)))) {
        currentSegmentPath = path;
        currentSegmentLesserKey = lesserKey;
        return (Dict<PrimaryKey, Dict<String, Object>>) ois.readObject();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private final void save() {
    try (ObjectOutputStream oos = new ObjectOutputStream(
      new FileOutputStream("../" + tableName + '/' + tableName + extension))) {
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

  public static void main(String[] args) {
    Table<String> userTable = new Table<String>("Users");
    userTable.addColumn("Address", String.class);
    userTable.addRow("Isaac").addRow("Bernie").addRow("Andres");
    userTable.addCell("Isaac", "Address", "Por algún lugar por ahí");
    userTable.addCell("Bernie", "Address", "Asoinfwoiniwefnoin oiasdn oai");
    userTable.addCell("Andres", "Address", "que chingue su madre el pri");
    System.out.println(userTable.getCell("Isaac", "Address"));
  }
}
