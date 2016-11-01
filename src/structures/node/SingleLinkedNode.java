package structures.node;

public class SingleLinkedNode<V> extends Node<V> {
  SingleLinkedNode<V> next;
  private static final long serialVersionUID = 13l;

  public SingleLinkedNode(V val) {
    super(val);
    this.next = null;
  }

  public SingleLinkedNode(V val, SingleLinkedNode<V> next) {
    super(val);
    this.next = next;
  }

  public SingleLinkedNode<V> next() {
    return this.next;
  }

  public void setNext(SingleLinkedNode<V> next) {
    this.next = next;
  }
}
