package database.table.column;

import java.io.Serializable;
import database.HarambException;
import database.table.Table;
import database.Database;
import database.table.relation.*;

public class Column implements Serializable {
  int index;
  Class<?> type;
  private static final long serialVersionUID = 24l;
  private Relation relation;

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

  public boolean hasRelation() {
    return relation != null;
  }

  public void removeRelation() {
    this.relation = null;
  }

  public RelationType relationType() throws HarambException {
    if (!this.hasRelation()) {
      throw new HarambException("Column has no relation");
    }
    return relation.type();
  }

  public Class<?> otherPrimaryKeyType() throws HarambException {
    if (!this.hasRelation()) {
      throw new HarambException("Column has no relation");
    }
    return this.relation.otherPrimaryKeyType();
  }

  public <PK extends Comparable<? super PK>> Table<PK> getRelatedTable(Database db) throws HarambException {
    Table<PK> t = db.getTable(this.relation.tableName(), this.relation.otherPrimaryKeyType());
    return t;
  }

  public void createRelation(Table<?> to, RelationType type) throws HarambException {
    if (hasRelation()) {
      throw new HarambException("Column already has a relation");
    }
    this.relation = new Relation(this, to, type);
  }
}
