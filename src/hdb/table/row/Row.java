package hdb.table.row;

import hdb.table.column.ColumnList;
import hdb.table.column.Column;
import hdb.HarambException;
import java.io.Serializable;
import java.util.List;

/**
* Interface of a Row in the HarambeDB hdb.
*
* <p>This class is a member of the
* <a href="{@docRoot}/index.html" target="_top">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
*/
public interface Row extends Serializable, Iterable<Object> {

  /**
  * Sets the value in the field of the column to the element specified
  * @param  col               The column where the element goes to
  * @param  element           The element to set
  * @param  <T>               The data type of the element to set
  * @throws HarambException   If the data types of the element and the column missmatch
  * @return                   This row, used to chain set calls
  */
  public <T> Row set(Column col, T element) throws HarambException;

  /**
  * Removes the value stored in the field 'col' (it is set to null)
  * @param  col The column of the field to remove
  */
  public void remove(Column col);

  /**
  * Get the value stored in the column
  * @param  column  The column of the field to get
  * @param  <T>     The data type of the column to get
  * @return         The value stored in the field with column 'column', null if there is no such column in the row
  */
  public <T> T get(Column column);

  /**
  * Prints the row
  */
  public void print();

  /**
  * Return the size of the row
  * @return The size of the row
  */
  public int size();
}
