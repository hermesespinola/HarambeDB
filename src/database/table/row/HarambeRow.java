package database.table.row;

import structures.list.ArrayLinearList;
import database.table.column.*;

public class HarambeRow implements Row {
  private ArrayLinearList<Object> row;
  private static final long serialVersionUID = 16L;

  public HarambeRow(ColumnList list) {
    row = new ArrayLinearList<>(list.size());
  }

  public <T> HarambeRow set(Column col, T element) throws Exception { // TODO: change to HarambException
    // TODO: test this.
    while (col.index() > row.size()) {
      row.add(null);
    }

    if ( col.type() != element.getClass() ) {
      throw new Exception("Element does not agree with column data type");
    }

    row.add(col.index(), element);

    return this;
  }

  public void remove(Column col) {
    while (col.index() > row.size()) {
      row.add(null);
    }
    row.set(col.index(), null);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(Column column) {
    return (T) row.get(column.index());
  }

  public String toString() {
    return row.toString();
  }
}
