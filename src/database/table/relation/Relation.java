package database.table.relation;

import database.table.column.Column;
import database.HarambException;
import database.table.Table;
import java.io.Serializable;

/**
* Represents a relation that points to a table.
*
* A relation can be one to one or one to many.
* This class should be used inside a column, so that that column points to
* another table through the relation.
* If the relation is one to one the column type should be the same as the type
* of the primary key of the related table. If the relation is one to many then
* the column type should be an array that holds elements of the same type as
* the type of the primary key of the related table.
*
* <p>This class is a member of the
* <a href="{@docRoot}/../index.html">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
* @see     Column
*/
public class Relation implements Serializable {

  /**
  * An enumeration that tells the type of relation that a column holds
  */
  public static enum Type implements Serializable {
    oneToOne,
    oneToMany;
  }

  /**
  * The type of relation (oneToOne or oneToMany)
  */
  Type type;

  /**
  * The name of the table the relation points to
  */
  String tableName;

  /**
  * The Class of the primary key of the related table
  */
  Class<?> otherPrimaryKeyType;
  private static final long serialVersionUID = 28l;

  /**
  * Creates a new relation of type 'type' from a Column to a table. Notice that
  * this new relation won't store the column, it is the Column the one that
  * stores the relation.
  * @param  from              The source of the relation.
  * @param  to                The endpoint of the relation
  * @param  type              The type of relation
  * @throws HarambException   If the types of the column and the table missmatch
  */
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

  /**
  * {@link Relation#type}
  */
  public Type type() {
    return this.type;
  }

  /**
  * {@link Relation#otherPrimaryKeyType}
  */
  public Class<?> otherPrimaryKeyType() {
    return this.otherPrimaryKeyType;
  }

  /**
  * {@link Relation#tableName}
  */
  public String tableName() {
    return this.tableName;
  }
}
