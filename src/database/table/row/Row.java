package database.row;

import java.util.List;
import database.column.ColumnList;
import database.column.Column;

public interface Row {
  ColumnList columns = null;
  public void add(); // add a new column
  public void remove(String columnName); // remove a Column
  public <T> T get(Column column);
}
