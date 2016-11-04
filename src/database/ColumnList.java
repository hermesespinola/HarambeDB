package database;

import structures.dict.LinkedDict;

interface ColumnList {
  LinkedDict<String, Column> columns = null; // Name -> index
  public <T> T cast();
}
