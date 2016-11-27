package database.table.column;

import java.io.Serializable;
import database.HarambException;
import database.table.Table;
import database.Database;
import database.table.relation.Relation;

/**
* Represents a Column in the HarambeDB database.
*
* This class is used along the row class to obtain a field in a row.
* Usually you also need to define a <a href="{@docRoot}/../path-to/ColumnList.html"> ColumnList </a>
* in order to create a row.
* The Row stores a list of elements and the column stores the index of the
* desired element and its data type, this way the row can store any value as
* long as it is an instance of the Object Class and when you try to retrieve it
* if the data type of the variable you are storing it into missmatch with the
* actual data type of the object then a HarambException is thrown.
*
* <p>This class is a member of the
* <a href="{@docRoot}/../index.html">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
*/
public class Column implements Serializable {

  /**
  * The index of the value this column stores in a row
  */
  int index;

  /**
  * The type of relation (oneToOne or oneToMany)
  */
  Class<?> type;
  private static final long serialVersionUID = 24l;

  /**
  * The relation of this column, if it has no relation then it is null
  */
  private Relation relation;

  public Column(int index, Class<?> type) {
    this.index = index;
    this.type = type;
  }

  /**
  * {@link Column#type}
  */
  public Class<?> type() {
    return this.type;
  }

  /**
  * {@link Column#index}
  */
  public int index() {
    return index;
  }

  /**
  * Tells if this column has a relation
  * @return  True if the column has a relation, false otherwise
  */
  public boolean hasRelation() {
    return relation != null;
  }

  /**
  * Deletes the relation in this column
  */
  public void removeRelation() {
    this.relation = null;
  }

  /**
  * Tells the type of the relation this column has
  * @throws HarambException   If this column has no relation
  * @return                   The Type of relation
  */
  public Relation.Type relationType() throws HarambException {
    if (!this.hasRelation()) {
      throw new HarambException("Column has no relation");
    }
    return relation.type();
  }

  /**
  * Tells Class of the primary key of the related table
  * @throws HarambException   If this column has no relation
  * @return                   The Class of the primary key of the related table
  */
  public Class<?> otherPrimaryKeyType() throws HarambException {
    if (!this.hasRelation()) {
      throw new HarambException("Column has no relation");
    }
    return this.relation.otherPrimaryKeyType();
  }

  /**
  * Obtains the related table
  * @throws HarambException   If this column has no relation
  * @return                   the related table
  */
  public <PK extends Comparable<? super PK>> Table<PK> getRelatedTable(Database db) throws HarambException {
    if (!this.hasRelation()) {
      throw new HarambException("Column has no relation");
    }
    Table<PK> t = db.getTable(this.relation.tableName(), this.relation.otherPrimaryKeyType());
    return t;
  }

  /**
  * Creates a new relation between this column and the specified table
  * @param  to                The table to relate
  * @param  type              The Type of relation
  * @throws HarambException   If there is a relation already
  */
  public void createRelation(Table<?> to, Relation.Type type) throws HarambException {
    if (hasRelation()) {
      throw new HarambException("Column already has a relation");
    }
    this.relation = new Relation(this, to, type);
  }
}
