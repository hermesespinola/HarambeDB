package database;

import java.util.List;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

// create the partition file when <init>
interface Partition<PrimaryKey> {
  int partitionNumber = 0; // An id for the partition
  public static final String extension = ".hbpt";

  // load the object from a .hbpt file, same as Table's loadPartition
  public static Partition<?> load(String tableName, int partitionNumber) { // then cast to Partition<PrimaryKey> in Table class
    try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
    new FileInputStream("../" + tableName + '/' + tableName + partitionNumber + extension)))) {
      return (Partition) ois.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  };
  // save the partition in a .hbpt file
  public void save(); // same as Table's saveCurrentPartition and savePartition
  public List<PrimaryKey> getPrimaryKeys();
  public void addRow(Row row);
  public Row getRow(PrimaryKey key);
  public void removeRow(PrimaryKey key);
}
