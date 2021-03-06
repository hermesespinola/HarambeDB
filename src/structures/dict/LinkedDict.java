package structures.dict;

import structures.list.ArrayLinearList;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Iterator;
import java.util.Arrays;

/**
* An implementation of the Dictionary interface using an array of linked lists
*
* <p>This class is a member of the
* <a href="{@docRoot}/index.html" target="_top">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
*/
@SuppressWarnings("rawtypes")
public class LinkedDict<Key, Val> implements Dict<Key, Val> {
  private DictNode<Key, Val>[] table;
  private int m; // table size;
  private int n; // amount of values in the table
  private float loadFactor;
  private int threshold;
  public static final int INIT_CAP = 5;
  public static final float DEF_LOAD = 0.75f;
  private static final long serialVersionUID = 2L;

  /**
   * Parametrized constructor. Sets initial size and load factor.
   *
   * @param initialSize initial size of the array
   * @param loadFactor maximum factor in order to resize
   */
  @SuppressWarnings("unchecked")
  public LinkedDict(int initialSize, float loadFactor) {
    if (loadFactor > 1 || loadFactor <= 0)
      throw new IllegalArgumentException("load factor must be a number between 0 (exclusive) and 1 (inclusive)");
    this.table = new DictNode[initialSize];
    this.m = initialSize;
    this.n = 0;
    this.loadFactor = loadFactor;
    this.threshold = (int) (initialSize * loadFactor);
  }

  /**
   * Parametrized constructor. Sets initial size and takes default load factor.
   *
   * @param initialSize initial size of the array
   */
  public LinkedDict(int initialSize) {
	  this(initialSize, DEF_LOAD);
  }

  /**
   * Default constructor. Takes default initial size and default load factor.
   */
  public LinkedDict() {
    this(INIT_CAP, DEF_LOAD);
  }


  public Val getValue(Key k) {
    checkKey(k);
    for (DictNode<Key,Val> x = table[hash(k)]; x != null; x = x.next) {
        if (x.key.equals(k)) return x.value;
    }
    return null;
  }

  public boolean contains(Key k) {
    checkKey(k);
    return getValue(k) != null;
  }

  public void add(Key k, Val v) {
    checkKey(k);
    if (n >= threshold) rehash();
    if (v == null) throw new IllegalArgumentException();
    int i = hash(k);
    for (DictNode<Key,Val> x = table[i]; x != null; x = x.next) {
        if (x.key.equals(k)) {
          Val prev = x.value;
          x.value = v;
          return;
        }
    }
    DictNode<Key, Val> first = new DictNode<>(k, v, table[i]);
    table[i] = first;
    n++;
  }

  public Val remove(Key k) {
    checkKey(k);
    DictNode<Key,Val> x = table[hash(k)];
    if (x.key.equals(k)) {
      Val v = x.value;
      table[hash(k)] = x.next;
      n--;
      return v;
    }
    while (x.next != null) {
      if (x.next.key.equals(k)) {
        Val v = x.next.value;
        x.next = x.next.next;
        n--;
        return v;
      }
      x = x.next;
    }
    return null;
  }

  public void clear() {
    for (DictNode<Key, Val> root : table) {
      root = null;
    }
  }

  @SuppressWarnings("unchecked")
  private void rehash() {
    DictNode<Key, Val>[] oldTable = new DictNode[m];
    System.arraycopy(this.table, 0, oldTable, 0, m);
    this.table = new DictNode[m*2];
    m*=2;
    n = 0;
    this.threshold = (int) (m * loadFactor);
    for (DictNode<Key, Val> x : oldTable) {
      for (; x != null; x = x.next) {
        add(x.key, x.value);
      }
    }
    oldTable = null;
  }

  public boolean isEmpty() {
    return n == 0;
  }

  public int getSize() {
    return n;
  }

  public int hash(Key k) {
    return k.hashCode() & 0x7FFFFFF % m;
  }

  public ArrayLinearList<Key> keys() {
    ArrayLinearList<Key> list = new ArrayLinearList<Key>();
    for (DictNode<Key, Val> x : table) {
      for (; x != null; x = x.next) {
        list.add(x.key);
      }
    }
    return list;
  }

  public ArrayLinearList<Val> values() {
    ArrayLinearList<Val> list = new ArrayLinearList<Val>();
    for (DictNode<Key, Val> x : table) {
      for (; x != null; x = x.next) {
        list.add(x.value);
      }
    }
    return list;
  }

  private void checkKey(Key k) {
    if (k == null) throw new NullPointerException();
  }

  private static class DictNode<Key, Val> implements Serializable {
    Key key;
    Val value;
    DictNode<Key, Val> next;
    private static final long serialVersionUID = 1L;

    public DictNode(Key k, Val v, DictNode<Key, Val> nextDictNode)  {
      this.key = k;
      this.value = v;
      this.next = nextDictNode;
    }

    public String toString() {
      return this.key + ": " + this.value;
    }
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{ ");
    for (DictNode<Key, Val> root : table) {
      DictNode<Key,Val> currentNode = root;
      while (currentNode != null) {
        sb.append(currentNode).append(", ");
        currentNode = currentNode.next;
      }
    }
    sb.setLength(sb.length() > 2 ? sb.length()-2 : sb.length());
    sb.append(" }");
    return sb.toString();
  }
}
