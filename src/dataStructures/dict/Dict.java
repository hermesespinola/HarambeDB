package dict;

import java.util.Iterator;

public interface Dict<K,V> {
  public void add(K key, V value);
  public V remove(K key);
  public V getValue(K key);
  public boolean contains(K key);
  public Iterable<K> keys();
  public Iterable<V> values();
  public boolean isEmpty();
  public int getSize();
  public void clear();
}
