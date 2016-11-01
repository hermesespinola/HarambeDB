package structures.node;
import java.io.Serializable;

public class Node<T> implements Serializable {
  T value;
  private static final long serialVersionUID = 07L;

  public Node(T value) {
    this.value = value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public T getValue() {
    return this.value;
  }
}
