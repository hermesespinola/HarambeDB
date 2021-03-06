package hdb.table.column;

import structures.dict.LinkedDict;
import structures.list.List;
import java.io.Serializable;
import java.util.Iterator;

/**
* Main implementation of interface ColumnList for HarambeDB hdb. This class
* uses a LinkedDict to represent the list of columns.
*.
* <p>This class is a member of the
* <a href="{@docRoot}/index.html" target="_top">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
* @see     Column
* @see     ColumnList
*/
public class HarambeColumnList implements ColumnList {
  /**
  * The List of columns, it is actually a Dictionary
  */
  LinkedDict<String, Column> columns = new LinkedDict<>();

  private static final long serialVersionUID = 19L;

  public int indexOf(String columnName) {
    return this.columns.getValue(columnName).index;
  }

  @SuppressWarnings("unchecked")
  public <T> Column get(String columnName) {
    return columns.getValue(columnName);
  }

  public <ColumnDataType> void add(String columnName, Column col) {
    this.columns.add(columnName, col);
  }

  public int size() {
    return columns.getSize();
  }

  public String toString() {
    return columns.keys().toString();
  }

  public Iterator<Column> iterator() {
    return columns.values().iterator();
  }

  public List<String> names() {
    return columns.keys();
  }
}
