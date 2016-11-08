package database.table.relationship;

import database.table.Table;
import database.table.Row;
import structures.list.ArrayLinearList;

public class Relationship<T> {
  protected Table<?> originTable;
  protected Table<?> destinyTable;
  protected String originField;
  protected String destinyField;
  protected boolean isMultiple;

  public Row getRow() {
    if (this.isMultiple) {
      throw new HarambException("Relationship is one to many. Use getRows() instead.");
    }
  }

  public ArrayLinearList<Row> getRows() {
    if (!this.isMultiple) {
      throw new HarambException("Relationship is one to one. Use getRow() instead.");
    }
  }
}
