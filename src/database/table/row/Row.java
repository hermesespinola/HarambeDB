package database.table.row;

import java.util.List;
import database.table.column.ColumnList;
import database.table.column.Column;
import java.io.Serializable;

public interface Row extends Serializable {
  public <T> Row set(Column col, T element) throws Exception; // add a new column
  public void remove(Column col); // remove a Column
  public <T> T get(Column column);
}
