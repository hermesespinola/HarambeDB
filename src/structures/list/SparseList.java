package structures.list;

import java.util.Iterator;
import structures.node.SparseNode;

public class SparseList<V> implements List<V> {
  private static class SparseListNode<V> extends SparseNode<V> {
    SparseListNode<V> nextNode;

    SparseListNode(int index, V val, SparseListNode<V> nextNode) {
      super(index, val);
      this.nextNode = nextNode;
    }

    SparseListNode<V> next() {
      return nextNode;
    }

    void setNext(SparseListNode<V> newNode) {
      this.nextNode = newNode;
    }
  }


  SparseListNode<V> firstNode;
  int size;
  V zero;

  public SparseList(V zeroValue) {
    this.firstNode = null;
    this.zero = zeroValue;
  }

  public boolean empty() {
    return firstNode == null;
  }

  public int size() {
    return this.size;
  }

  public V get(int index) {
    if (empty() || index < 0 || index > size-1)
      throw new IndexOutOfBoundsException();

    SparseListNode<V> currentNode = firstNode;
    while (currentNode != null) {
      if (currentNode.index() > index) {
        return zero;
      } else if (currentNode.index() == index) {
        return currentNode.getValue();
      }
      currentNode = currentNode.next();
    }
    return zero;
  }

  public int indexOf(V x) {
    if (empty())
      return -1;
    SparseListNode<V> currentNode = firstNode;
    while (currentNode != null) {
      if (currentNode.getValue().equals(x)) {
        return currentNode.index();
      }
      currentNode = currentNode.next();
    }
    return -1;
  }

  public V remove(int index) {
    if (empty() || index < 0 || index > size-1)
      throw new IndexOutOfBoundsException();

    SparseListNode<V> previousNode = firstNode;
    SparseListNode<V> currentNode = firstNode;
    while (currentNode != null) {
      if (currentNode.index() > index) {
        return zero;
      } else if (currentNode.index() == index) {
        previousNode.setNext(currentNode.next());
        V val = currentNode.getValue();
        currentNode = null;
        return val;
      }
      previousNode = currentNode;
      currentNode = currentNode.next();
    }
    return zero;
  }

  public void add(int index, V element) {
    if (empty()) {
      this.firstNode = new SparseListNode<V>(index, element, null);
      return;
    }
    SparseListNode<V> currentNode = firstNode;
    while (currentNode.next() != null && index != currentNode.index() && index < currentNode.next().index()) {
      currentNode = currentNode.next();
    }
    if (currentNode.index() == index) {
      currentNode.setValue(element);
    } else {
      SparseListNode<V> newNode = new SparseListNode<V>(index, element, currentNode.next());
      currentNode.setNext(newNode);
      if (index >= size) {
        this.size = index+1;
      }
    }
  }

  public void add(V element) {
    add(size+1, element);
  }

  public Iterator<V> iterator() {
    return new SparseListIterator();
  }

  private class SparseListIterator implements Iterator<V> {
    SparseListNode<V> next;

    SparseListIterator() {
      this.next = firstNode;
    }

    public V next() {
      V val = next.getValue();
      next = next.next();
      return val;
    }

    public boolean hasNext() {
      return next != null;
    }
  }
}
