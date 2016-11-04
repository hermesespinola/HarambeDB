package database;

import java.util.List;

public interface Row {
  ColumnList columns = null;
  public void add(); // add a new column
  public void remove(String columnName); // remove a Column
  public <T> T get(Column column);
}
