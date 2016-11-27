package database.table.column;

import structures.dict.LinkedDict;
import java.io.Serializable;

/**
* Represents a list of colums to be stored in a Table in the HarambeDB database.
*
* This class is used to represent the columns in a Table.
* You can add new columns in the database even if you have already added row to
* it and the rows should update their fields to match the columns specified here.
*
* It is worth to mention that the column itself does not store the name of that
* particular column, instead they are stored in a ColumnList, that means that
* the Class Column and the Class ColumnList depend on each other, though this
* is considered a bad OOP practice, this way we avoid data redundancy.
*
* <p>This class is a member of the
* <a href="{@docRoot}/../index.html">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
* @see     Column
*/
public interface ColumnList extends Serializable, Iterable<Column> {

  /**
  * Adds a column to the column list
  * @param  columnName  The name of the column to add
  * @param  col         The actual column containing its index and its data type
  */
  public <T> void add(String columnName, Column col);

  /**
  * Retrieve the column with the specified name
  * @param  columnName  The name of the column to get
  * @return             The column with the specified name, null if there is no column with that name
  */
  public <T> Column get(String columnName);

  /**
  * Retrieve the index value that is stored in the column, not the index of the
  * column in the column list
  * @param  columnName  The name of the column to get
  * @return             The index value of the column
  */
  public int indexOf(String columnName);

  /**
  * Tells the size of the list
  * @return             The size of the list
  */
  public int size();
}
