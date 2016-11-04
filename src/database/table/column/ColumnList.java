package database.column;

import structures.dict.LinkedDict;

public interface ColumnList {
  LinkedDict<String, Column> columns = null; // Name -> index
  public <T> T cast();
}
