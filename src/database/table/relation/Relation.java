package database.table.relation;

import database.HarambException;
import database.table.column.Column;
import database.table.Table;
import java.io.Serializable;

public class Relation implements Serializable {

  public static enum Type implements Serializable {
    oneToOne,
    oneToMany;
  }

  Type type;
  String tableName;
  Class<?> otherPrimaryKeyType;
  private static final long serialVersionUID = 28l;

  public Relation(Column from, Table<?> to, Type type) throws HarambException {
    if (type == Type.oneToMany) {
      if (!from.type().isArray()) {
        throw new HarambException("Column data type must be an array to create a one to many relation");
      } else if (!String[].class.getComponentType().isAssignableFrom(String.class)) {
        throw new HarambException("Column array and Talbe data types must agree.");
      }
    } else if (!from.type().isAssignableFrom(to.getPrimaryKeyType())) {
      throw new HarambException("Column and Talbe data types must agree.");
    }

    this.type = type;
    this.tableName = to.name();
    this.otherPrimaryKeyType = to.getPrimaryKeyType();
  }

  public Type type() {
    return this.type;
  }

  public Class<?> otherPrimaryKeyType() {
    return this.otherPrimaryKeyType;
  }

  public String tableName() {
    return this.tableName;
  }
}
