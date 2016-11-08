package database.table.column;

import java.io.Serializable;

public class Column implements Serializable {
  int index;
  Class<?> type;
  private static final long serialVersionUID = 24l;

  public Column(int index, Class<?> type) {
    this.index = index;
    this.type = type;
  }

  public Class<?> type() {
    return this.type;
  }

  public int index() {
    return index;
  }
}
