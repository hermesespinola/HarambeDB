package structures.stack;

import structures.node.Node;
import java.util.EmptyStackException;

public class LinkedStack<T> implements Stack<T> {
  NodeStack<T> topNode = null;
  public LinkedStack() {};

  public T peek() {
    return this.empty() ? null : this.topNode.getValue();
  }

  public void push(T element) {
    this.topNode = new NodeStack<T>(element, this.topNode);
  }

  public T pop() throws EmptyStackException {
    try {
      T ret = this.topNode.getValue();
      this.topNode = this.topNode.nextNode;
      return ret;
    } catch (Exception e) {
      throw new EmptyStackException();
    }
  }

  public String toString() {
    StringBuilder sb = new StringBuilder('[');
    NodeStack<T> current = this.topNode;
    while (current != null) {
      sb.append("[ "); sb.append(current.getValue()); sb.append(" ]");
      current = current.nextNode;
    }
    return sb.toString();
  }

  public boolean empty() {
    return this.topNode == null;
  }

  static class NodeStack<T> extends Node<T> {
    private static final long serialVersionUID = 27L;
    NodeStack<T> nextNode;
    NodeStack(T value, NodeStack<T> nextNode) {
      super(value);
      this.nextNode = nextNode;
    }
  }
}
