package database.table;

import structures.list.List;
import structures.dict.Dict;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import database.table.row.Row;
import java.io.Serializable;

// create the partition file when <init>
public interface Partition<PrimaryKey extends Comparable<? super PrimaryKey>> {
  static final String extension = ".hbpt";
  static final String prefix = "pt";

  // load the object from a .hbpt file, same as Table's loadPartition
  @SuppressWarnings("unchecked")
  public static <K extends Comparable<? super K>> Partition<K> load(String tablePath, int partitionNumber) {
    try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
    new FileInputStream(tablePath + prefix + partitionNumber + extension)))) {
      return (Partition<K>) ois.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  };
  // save the partition in a .hbpt file
  public void save() throws IOException; // same as Table's saveCurrentPartition and savePartition
  public String path(); // return the path to the partition file
  public List<PrimaryKey> getKeys();
  public void addRow(PrimaryKey key, Row row);
  public Row getRow(PrimaryKey key);
  public void removeRow(PrimaryKey key);
  public int partitionNumber();
  public int size();
  public Dict<PrimaryKey, Row> rows();
}