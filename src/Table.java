import structures.dict.Dict;
import structures.dict.LinkedDict;
import structures.list.List;
import structures.list.ArrayLinearList;

public class Table<PrimaryKey> {
  private Dict<PrimaryKey, Dict<String, Object>> table;
  private Dict<String, Object> columns;

  public Table() {
    table = new LinkedDict<PrimaryKey, Dict<String, Object>>();
    columns = new LinkedDict<String, Object>();
  }

  public Table<PrimaryKey> addColumn(String name, Object type) {
    columns.add(name, type);
    return this;
  }

  public Table<PrimaryKey> addRow(PrimaryKey key) {
    table.add(key, new LinkedDict<String, Object>());
    return this;
  }

  public Dict<String, Object> getRow(PrimaryKey key) {
    Dict<String, Object> row = table.getValue(key);
    if (row == null) {
      throw new RuntimeException("No such row");
    }
    return row;
  }

  public Object getCell(PrimaryKey key, String column) {
    checkColumn(column);
    Dict<String, Object> row = getRow(key);
    return row.getValue(column);
  }

  public Table<PrimaryKey> addCell(PrimaryKey key, String column, Object value) {
    checkColumn(column);
    checkColumnType(column, value);
    Dict<String, Object> row = getRow(key);
    row.add(column, value);
    return this;
  }

  private final void checkColumn(final String column) {
    if (columns.getValue(column) == null) {
      throw new RuntimeException("No such Column");
    }
  }

  private final void checkColumnType(final String column, final Object value) {
    if (columns.getValue(column) != value.getClass()) {
      throw new RuntimeException("Column datatype missmatch");
    }
  }

  public static void main(String[] args) {
    Table<String> userTable = new Table<String>();
    userTable.addColumn("Address", String.class);
    userTable.addRow("Isaac").addRow("Bernie").addRow("Andres");
    userTable.addCell("Isaac", "Address", "Por algún lugar por ahí");
    System.out.println(userTable.getCell("Isaac", "Address"));
  }
}
