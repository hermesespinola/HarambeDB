package database.table.row;

import structures.list.ArrayLinearList;
import database.HarambException;
import database.table.column.*;
import java.util.Iterator;

/**
* Main implementation of interface Row for HarambeDB database.
* This row implementation stores an array of objects to store the values of each
* row, and the Column class stores the index of the array where its value is
* stored.
*
* Note that the row does not store the primary key, it just stores the values
* of the columns linked to that primary key. The primary key is stored in the
* partition using a dictionary of primary keys mapping to row objects, as it
* would be redundant to sore the primary key twice.
*
* <p>This class is a member of the
* <a href="{@docRoot}/../index.html">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
* @see     Row
*/
public class HarambeRow implements Row {
  /**
  * The row in which the fields are stored
  */
  private ArrayLinearList<Object> row;
  private static final long serialVersionUID = 16L;

  /**
  * Creates a new row with the ColumnList specified
  */
  public HarambeRow(ColumnList list) {
    row = new ArrayLinearList<>(list.size());
  }

  public <T> HarambeRow set(Column col, T element) throws HarambException {
    while (col.index() > row.size()) {
      row.add(null);
    }

    if ( col.type() != element.getClass() ) {
      throw new HarambException("Element's type of '" + element + "' does not agree with column data type:" + col.type());
    }

    row.add(col.index(), element);

    return this;
  }

  public void remove(Column col) {
    while (col.index() > row.size()) {
      row.add(null);
    }
    row.set(col.index(), null);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(Column column) {
    return (T) row.get(column.index());
  }

  public Iterator<Object> iterator() {
    return row.iterator();
  }

  public String toString() {
    return row.toString();
  }
}
