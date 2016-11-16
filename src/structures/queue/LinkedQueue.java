package structures.queue;

public class LinkedQueue<V> implements Queue<V> {
  private QueueNode<V> front;
  private QueueNode<V> rear;
  int size;

  public LinkedQueue() {
    size = 0;
  }

  public boolean empty() {
    return size == 0;
  }

  public V front() {
    return front.value;
  }

  public V rear() {
    return rear.value;
  }

  public int size() {
    return size;
  }

  public void enqueue(V object) {
    if (size == 0) {
      front = new QueueNode<V>(object);
      rear = front;
    } else {
      rear.next = new QueueNode<V>(object);
      rear = rear.next;
    }
    size++;
  }

  public V dequeue() {
    V ret = null;
    if (front != null) {
      ret = front.value;
      front = front.next;
    }
    size--;
    return ret;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[ ");
    for (QueueNode<V> n = front; n != null; n = n.next) {
      sb.append(n.value).append(' ');
    }
    return sb.append(']').toString();
  }

  public static void main(String[] args) {
    LinkedQueue<Integer> q = new LinkedQueue<>();
    for (int i = 0; i < 10; i++) {
      q.enqueue(i);
    }
    while (!q.empty()) {
      System.out.println(q.dequeue());
    }
  }

  private static class QueueNode<V> {
    protected V value;
    protected QueueNode<V> next;

    protected QueueNode(V value) {
      this.value = value;
      this.next = next;
    }

    protected void setNext(QueueNode<V> next) {
      this.next = next;
    }
  }
}
