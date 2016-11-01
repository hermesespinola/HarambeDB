package structures.node;

public class DoublyLinkedNode<V> extends SingleLinkedNode<V> {
  DoublyLinkedNode<V> previous;

  public DoublyLinkedNode(V val) {
    super(val);
    this.previous = null;
  }

  public DoublyLinkedNode(V val, DoublyLinkedNode<V> next) {
    super(val, next);
    this.previous = null;
  }

  public DoublyLinkedNode(V val, DoublyLinkedNode<V> next, DoublyLinkedNode<V> prev) {
    super(val, next);
    this.previous = prev;
  }

  @Override
  public DoublyLinkedNode<V> next() {
    return (DoublyLinkedNode<V>) super.next();
  }

  public DoublyLinkedNode<V> previous() {
    return this.previous;
  }

  public void setPrevious(DoublyLinkedNode<V> prev) {
    this.previous = previous;
  }
}
