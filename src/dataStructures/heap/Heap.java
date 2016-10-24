package heap;

import java.util.Iterator;

public interface Heap <V> {
  public void clear();
  public void insert(V value);
  public boolean isEmpty();
  public boolean isFull();
  public V peek();
  public V pop();
  public int size();
  public String toString();
}
