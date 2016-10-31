package dict;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
public class LinkedDict<Key, Val> implements Dict<Key, Val> {
  private DictNode<Key, Val>[] table;
  private int m; // table size;
  private int n; // amount of values in the table
  private float loadFactor;
  private int threshold;
  public static final int INIT_CAP = 5;
  public static final float DEF_LOAD = 0.75f;

  /**
   * @param  initialSize the initial size of the table
   * @param  loadFactor maximum amount of elements in the table
   * @return      A hash table instance using LinkedList
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
   * @return      A hash table instance using LinkedList
  */
  public LinkedDict() {
    this(INIT_CAP, DEF_LOAD);
  }

  /**
   * @param  k the key of the element
   * @return      A value in the hash table
  */
  public Val getValue(Key k) {
    checkKey(k);
    for (DictNode<Key,Val> x = table[hash(k)]; x != null; x = x.next) {
        if (x.key.equals(k)) return x.value;
    }
    return null;
  }

  /**
   * @param  k the key to check
   * @return      true if the key is in the hash table, false otherwise
  */
  public boolean contains(Key k) {
    checkKey(k);
    return getValue(k) != null;
  }

  /**
   * maps the specified value in the hash table using the provided key.
   * @param  k the key to map the value
   * @param  v the value to map
   * @return      The previous value mapped to that key, null if the key was not mapped already
  */
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

  /**
   * @param  k  The key to remove
   * @return      The value mapped to the key provided, null if the key is not in the table
  */
  public Val remove(Key k) {
    checkKey(k);
    for (DictNode<Key,Val> x = table[hash(k)]; x != null; x = x.next) {
        if (x.key.equals(k)) {
          Val v = x.value;
          x = x.next;
          n--;
          return v;
        };
    }
    return null;
  }

  /**
   * erases the contents of the table
  */
  public void clear() {
    // borra todo al chile
    for (DictNode<Key, Val> root : table) {
      root = null;
    }
  }

  /**
   *  duplicates the size of the table and rehashes all the elements in the new table
  */
  @SuppressWarnings("unchecked")
  private void rehash() {
    // Create  a copy of the old table and set this.table to a new table so
    // I'm able to reuse the add function
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

  /**
   * @return if the table has no elements
  */
  public boolean isEmpty() {
    return n == 0;
  }

  /**
   * @return      The number of elements in the table
  */
  public int getSize() {
    return n;
  }

  /**
   * @return      An integer that maps to an index in the table
  */
  public int hash(Key k) {
    return k.hashCode() & 0x7FFFFFF % m;
  }

  /**
   * @return      An interator containing the keys of the hash table
  */
  public ArrayList<Key> keys() {
    ArrayList<Key> list = new ArrayList<Key>();
    for (DictNode<Key, Val> x : table) {
      for (; x != null; x = x.next) {
        list.add(x.key);
      }
    }
    return list;
  }

  /**
   * @return      An interator containing the values of the hash table
  */
  public ArrayList<Val> values() {
    ArrayList<Val> list = new ArrayList<Val>();
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

  private static class DictNode<Key, Val> {
    Key key;
    Val value;
    DictNode<Key, Val> next;

    public DictNode(Key k, Val v, DictNode<Key, Val> nextDictNode)  {
      this.key = k;
      this.value = v;
      this.next = nextDictNode;
    }
  }

  public static void main(String[] args) {
    LinkedDict<String, String> dict = new LinkedDict<>();
    System.out.println(dict.isEmpty());
    dict.add("Lucio", "Pez");
    System.out.println(dict.getValue("Lucio"));
    dict.add("Pichón", "Pajaro");
    dict.add("Tres", "3");
    dict.add("Vaso", "Vierre");
    System.out.println(dict.threshold);
    dict.add("Verde", "Vierre");
    dict.add("Pez", "Poisson");
    dict.add("Wololo", "AOfE");
    dict.add("qwerty", "asdfg");

    System.out.println();
    for (String k : dict.keys()) {
      System.out.println(k);
    }
    System.out.println();
    for (String v : dict.values()) {
      System.out.println(v);
    }
  }
}
