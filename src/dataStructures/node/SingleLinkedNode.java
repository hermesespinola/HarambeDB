package node;

public class SingleLinkedNode<V> extends Node<V> {
  SingleLinkedNode<V> next;

  public SingleLinkedNode(V val) {
    super(val);
    this.next = null;
  }

  public SingleLinkedNode(V val, SingleLinkedNode<V> next) {
    super(val);
    this.next = next;
  }

  public SingleLinkedNode<V> getNext() {
    return this.next;
  }

  protected void setNext(SingleLinkedNode<V> next) {
    this.next = next;
  }
}
