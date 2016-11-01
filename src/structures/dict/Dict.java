package structures.dict;

import java.util.Iterator;
import java.io.Serializable;
import java.util.List;

public interface Dict<K,V> extends Serializable  {
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
