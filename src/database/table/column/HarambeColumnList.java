package database.table.column;

import structures.dict.LinkedDict;
import java.util.Iterator;
import java.io.Serializable;

public class HarambeColumnList implements ColumnList {
  LinkedDict<String, Column> columns = new LinkedDict<>(); // Name -> index
  protected boolean hasRelation;
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
}
