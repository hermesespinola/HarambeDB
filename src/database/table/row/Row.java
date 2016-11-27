package database.table.row;

import database.table.column.ColumnList;
import database.table.column.Column;
import database.HarambException;
import java.io.Serializable;
import java.util.List;

/**
* Interface of a Row in the HarambeDB database.
*
* <p>This class is a member of the
* <a href="{@docRoot}/../index.html">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
* @see     HarambeRow
*/
public interface Row extends Serializable, Iterable<Object> {

  /**
  * sets the field 'col' of this row to 'element'
  * @param  col             The column of the field to set
  * @param  element         The new value of the field 'col' in row
  * @throws HarambException If element and column types missmatch
  * @return this row
  */
  public <T> Row set(Column col, T element) throws HarambException;

  /**
  * Removes the value stored in the field 'col' (it is set to null)
  * @param  col The column of the field to remove
  */
  public void remove(Column col);

  /**
  * Get the value stored in the column
  * @return The value stored in the field with column 'column', null if there is no such column in the row
  */
  public <T> T get(Column column);
}
