package database.table;

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import database.HarambException;
import java.io.FileInputStream;
import database.table.row.Row;
import java.io.Serializable;
import structures.dict.Dict;
import structures.list.List;
import java.io.IOException;
import java.io.IOException;

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
  public void save() throws HarambException; // same as Table's saveCurrentPartition and savePartition
  public String path(); // return the path to the partition file
  public List<PrimaryKey> getKeys();
  public void addRow(PrimaryKey key, Row row) throws HarambException;
  public Row getRow(PrimaryKey key);
  public boolean removeRow(PrimaryKey key) throws HarambException;
  public int partitionNumber();
  public int size();
  public Dict<PrimaryKey, Row> rows();
}
