package structures.node;

public class SparseNode<T> extends Node<T> {
  protected int index;
  private static final long serialVersionUID = 16L;

  public SparseNode(int index, T val) {
    super(val);
    this.index = index;
  }

  public int index() {
    return this.index;
  }

  public String toString() {
    return '[' + this.index + "] = " + this.getValue();
  }
}
