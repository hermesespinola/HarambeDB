package structures.stack;

public interface Stack<V> {
  public boolean empty();
  public V peek();
  public void push(V object);
  public V pop();
}
