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

/**
* A partition interface that defines the required methods for a class to
* correctly work with the Table class.
*
* The partition should save the .hbpt file inside the table directory and
* contain an unique ID to identify the partition from other partitions, as the
* partition file is named with the prefix pt plus the partition ID and the
* partition extension.
*
* Due to the fact that the row containing the minimum key could be removed from
* a partition we need to either sort the keys in the partition or store the
* sorted keys of the partition in order to update the minimum key in the AVL
* tree. The current implementation of the partition stores the primary keys in
* a sorted list, this duplicates the space complexity of the PrimaryKey column
* but avoid performing a sort operation over the whole partition. A new class
* implementing the Partition interface performing the sorting should be
* relatively easy to write and should be used if the THRESHOLD is reasonably small.
*
* <p>This class is a member of the
* <a href="{@docRoot}/../index.html">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
* @see     Table
* @see     Row
*/
public interface Partition<PrimaryKey extends Comparable<? super PrimaryKey>> {

  /**
  * The extension of the partition files
  */
  static final String extension = ".hbpt";

  /**
  * The prefix of the partition file name
  */
  static final String prefix = "pt";

  /*
  * Load the object from a .hbpt file
  * @param  tablePath         The path to the table directory where the
  * partition file is to be stored, if an error occurs it prints the stack
  * trace, but won't throw the Exception
  * @param  partitionNumber   The unique ID of the partition
  * @return                   The partition Object readed from the file, null if there is no partition with that ID or if there is an error reading the file
  */
  @SuppressWarnings("unchecked")
  public static <K extends Comparable<? super K>> Partition<K> load(String tablePath, int partitionNumber) {
    try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
    new FileInputStream(tablePath + prefix + partitionNumber + extension)))) {
      return (Partition<K>) ois.readObject();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  };

  /**
  * Save the partition in a .hbpt file
  * @throws HarambException If there is an error writing the partition file
  */
  public void save() throws HarambException;

  /**
  * The path of to the partition file
  * @return The path to the partition file
  */
  public String path();

  /**
  * A sorted list containing the primary keys of the partition
  * @return A sorted list containing the primary keys of the partition
  */
  public List<PrimaryKey> getKeys();

  /**
  * Adds a row to the partition
  * @param  key               The primary of the row to add
  * @param  row               The row to add to the partition
  * @throws HarambException   If the primary key is already in the partition
  */
  public void addRow(PrimaryKey key, Row row) throws HarambException;

  /**
  * Retrieves the row with the specified primary key
  * @param  key   The primary of the row to get
  * @return       The row with primary key equals to key, null if there is no such row
  */
  public Row getRow(PrimaryKey key);

  /**
  * Removes a row from the partition
  * @param  key               The primary of the row to remove
  * @throws HarambException   If the primary key is already in the partition
  * @return                   If the removed key was the minimum key in the partition
  */
  public boolean removeRow(PrimaryKey key) throws HarambException;

  /**
  * Retrieves the partition ID
  * @return   The partition ID
  */
  public int partitionNumber();

  /**
  * Retrieves the number of rows in the partition
  * @return   The number of rows in the partition
  */
  public int size();

  /**
  * The dictionary containing all the rows in the partition
  * @return   The dictionary containing all the rows in the partition
  */
  public Dict<PrimaryKey, Row> rows();
}
