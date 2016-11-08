package database.table.column;

import structures.dict.LinkedDict;
import java.io.Serializable;

public interface ColumnList extends Serializable {
  public <T> void add(String columnName, Column col);
  public <T> Column get(String columnName);
  public int indexOf(String columnName);
  public int size();
}
