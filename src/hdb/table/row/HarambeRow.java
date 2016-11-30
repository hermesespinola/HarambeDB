package hdb.table.row;

import structures.list.ArrayLinearList;
import hdb.HarambException;
import hdb.table.column.*;
import java.util.Iterator;
import java.util.Arrays;

/**
* Main implementation of interface Row for HarambeDB hdb.
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
* <a href="{@docRoot}/index.html" target="_top">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
* @see     Row
* @see     Column
* @see     ColumnList
* @see     HarambeColumnList
*/
public class HarambeRow implements Row {
  /**
  * The row in which the fields are stored
  */
  private ArrayLinearList<Object> row;
  private static final long serialVersionUID = 16L;

  /**
  * Creates a new row with the ColumnList specified
  * @param  list  The column list used to create the row
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
  public <T> T get(Column col) {
    while (col.index() > row.size()) {
      row.add(null);
    }
    return (T) row.get(col.index());
  }

  public Iterator<Object> iterator() {
    return row.iterator();
  }

  public String toString() {
    return row.toString();
  }

  public int size() {
    return this.row.size();
  }

  public void print() {
    for (Object field : row) {
      if (field.getClass().isArray())
        System.out.print(Arrays.toString((Object[])field) + " ");
      else
        System.out.print(field + " ");
    }
    System.out.println();
  }
}
