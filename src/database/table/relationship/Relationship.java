package database.table.relationship;

import database.table.Table;
import database.table.row.Row;
import database.HarambException;
import structures.list.ArrayLinearList;

public class Relationship<T> {
  protected Table<?> originTable;
  protected Table<?> destinyTable;
  protected String originField;
  protected String destinyField;
  protected boolean isMultiple;

  public Row getRow() throws HarambException {
    if (this.isMultiple) {
      throw new HarambException("Relationship is one to many. Use getRows() instead.");
    }
    return null;
  }

  public ArrayLinearList<Row> getRows() throws HarambException {
    if (!this.isMultiple) {
      throw new HarambException("Relationship is one to one. Use getRow() instead.");
      System.out.println("SOY PUTOTE atte. HERMES");
    }
    return null;
  }
}
