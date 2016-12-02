package structures.list;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import structures.node.DoublyLinkedNode;
import java.io.Serializable;

public class DoublyLinkedList<T> implements List<T>, Serializable {
  int size;
  DoublyLinkedNode<T> first;
  DoublyLinkedNode<T> last;
  private static final long serialVersionUID = 18L;

  public DoublyLinkedList() {
    this.first = null;
    this.last = null;
    this.size = 0;
  }

  public ListIterator<T> iterator() {
    return new DoublyLinkedListIterator();
  }

  private void checkIndex(int index) {
    if (index < 0 || index > size) {
      throw new IndexOutOfBoundsException();
    }
  }

  public ListIterator<T> getIterator(int index) {
    checkIndex(index);
    return new DoublyLinkedListIterator(index);
  }

  public boolean empty() {
    return this.size == 0;
  }

  public int size() {
    return this.size;
  }

  public T get(int index) {
    checkIndex(index);
    return getNode(index).getValue();
  }

  private DoublyLinkedNode<T> getNode(int index) {
    DoublyLinkedNode<T> n = this.first;
    while (index > 0) {
      n = n.next();
      index--;
    }
    return n;
  }

  private void addFirst(T value) {
    DoublyLinkedNode<T> newNode = new DoublyLinkedNode<T>(value, this.first);
    if (this.empty()) {
      this.last = newNode;
    } else {
      this.first.setPrevious(newNode);
    }
    this.first = newNode;
    this.size++;
  }

  private void addLast(T value) {
    DoublyLinkedNode<T> newNode = new DoublyLinkedNode<T>(value, null, this.last);
    if (this.empty()) {
      this.first = newNode;
    } else {
      this.last.setNext(newNode);
    }
    this.last = newNode;
    this.size++;
  }

  public int indexOf(T x) {
    DoublyLinkedNode<T> currentNode = this.first;
    int i = 0;
    while (currentNode.getValue() != null) {
      if (currentNode.getValue().equals(x)) {
        return i;
      }
      currentNode = currentNode.next();
      i++;
    }
    return -1;
  }

  public T remove(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException();
    }
    DoublyLinkedNode<T> toRemove = getNode(index);
    if (index == 0) first = toRemove.next();
    else toRemove.previous().setNext(toRemove.next());
    if (index == this.size-1) last = toRemove.previous();
    else toRemove.next().setPrevious(toRemove.previous());
    size--;
    T r = toRemove.getValue();
    return r;
  }

  public void add(int index, T element) {
    checkIndex(index);
    if (index == 0) {
      addFirst(element);
    } else if (index == this.size) {
      addLast(element);
    } else {
      DoublyLinkedNode<T> nextNode = getNode(index);
      DoublyLinkedNode<T> newNode = new DoublyLinkedNode<T>(element, nextNode, nextNode.previous());
      nextNode.previous().setNext(newNode);
      nextNode.setPrevious(newNode);
      size++;
    }
  }

  public void add(T element) {
    addLast(element);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder("[ ");
    DoublyLinkedNode<T> node = this.first;
    while (node != null) {
      sb.append(node.getValue()).append(' ');
      node = node.next();
    }
    sb.append(']');
    return sb.toString();
  }

  class DoublyLinkedListIterator implements ListIterator<T> {
    DoublyLinkedNode<T> next;
    DoublyLinkedNode<T> lastVisited = null;
    int nIndex;

    public DoublyLinkedListIterator() {
      this.next = first;
      this.nIndex = 0;
    }

    public DoublyLinkedListIterator(int index) {
      if (index == size) {
        this.next = null;
      } else {
        this.next = getNode(index);
      }
      this.nIndex = index;
    }

    public void add(T el) {
      DoublyLinkedNode<T> newNode = new DoublyLinkedNode<T>(el);
      if (this.next == null) {
        first = newNode;
        last = newNode;
      } else {
        if (this.hasPrevious()) {
          this.next.previous().setNext(newNode);
        } else {
          first = newNode;
        }
        newNode.setNext(this.next);
        if (!this.hasNext()) {
          last = newNode;
        }
      }
      size++;
    }

    public void set(T el) {
      this.next.setValue(el);
    }

    public void remove() {
      DoublyLinkedNode<T> toRemove = this.next.previous();
      if (toRemove.previous() == null) first = this.next;
      else toRemove.previous().setNext(this.next);
      if (toRemove.next() == null) last = toRemove.previous();
      else this.next.setPrevious(toRemove.previous());
      toRemove = null;
      size--;
    }

    public int previousIndex() {
      return this.nIndex - 1;
    }

    public int nextIndex() {
      return this.nIndex;
    }

    public T previous() {
      if (!this.hasPrevious())
        throw new NoSuchElementException();
      if (this.next == null)
        this.next = last;
      else
        this.next = this.next.previous();

      this.lastVisited = this.next;
      this.nIndex--;
      return lastVisited.getValue();
    }

    public T next() {
      if (!this.hasNext()) {
        throw new NoSuchElementException();
      }
      lastVisited = this.next;
      this.nIndex++;
      this.next = this.next.next();
      return this.lastVisited.getValue();
    }

    public boolean hasNext() {
      return this.next != null;
    }

    public boolean hasPrevious() {
      return this.nIndex > 0;
    }
  }

  public static void main(String[] args) {
    DoublyLinkedList<String> list = new DoublyLinkedList<>();
    list.add("Uno");
    list.add("Dos");
    list.add("Tres");
    list.add("Cuatro");
    list.add("Cinco");
    list.add("Seis");
    System.out.println(list);
    list.remove(list.indexOf("Cuatro"));
    System.out.println(list);
  }
}
