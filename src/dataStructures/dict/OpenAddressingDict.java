package dict;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("rawtypes")
public class OpenAddressingDict<K, V> implements Dict<K, V> {
      private final static int DEF_SIZE = 128;
      public static final float DEF_LOAD = 0.75f;
      private int m;
      Entry<K, V>[] table;
      private int n; // amount of values in the table
      private long a, b, p;
      private float loadFactor;
      private int threshold;

      @SuppressWarnings("unchecked")
      public OpenAddressingDict(int initialSize, float loadFactor) {
        this.m = initialSize;
        this.n = 0;
        this.loadFactor = loadFactor;
        this.threshold = (int) (initialSize * loadFactor);
        table = (Entry<K, V>[])new Entry[m];
        a = new BigInteger(Integer.toString(ThreadLocalRandom.current().nextInt(1, 1001))).nextProbablePrime().longValue();
        b = new BigInteger(Integer.toString(ThreadLocalRandom.current().nextInt(0, 1001))).nextProbablePrime().longValue();
        p = new BigInteger(Integer.toString(m)).nextProbablePrime().longValue();
      }

      public OpenAddressingDict(int initialSize) {
        this(initialSize, DEF_LOAD);
      }

      public OpenAddressingDict() {
        this(DEF_SIZE, DEF_LOAD);
      }

      @SuppressWarnings("unchecked")
      private void resize() {
        Entry<K,V>[] oldTable = (Entry<K, V>[])new Entry[m];
        System.arraycopy(this.table, 0, oldTable, 0, m);
        n = 0;
        m *= 2;
        p = new BigInteger(Integer.toString(m)).nextProbablePrime().longValue();
        this.table = (Entry<K, V>[])new Entry[m];
        threshold = (int)(m * loadFactor);

        for (Entry<K,V> e : oldTable) {
          if (e != null) {
            this.add(e.key, e.value);
          }
        }
        oldTable = null;
      }

      private int hash(K key) {
        return (int) ((a * (key.hashCode() & 0x7FFFFFF) + b) % p) % m;
      }

      public V getValue(K key) {
        int h = hash(key);
        while (table[h] != null && !table[h].key.equals(key)) {
          h = (h + 1) % m;
        }
        if (table[h] == null)
          throw new NoSuchElementException();
        else
          return table[h].value;
      }

      public void add (K key, V value) {
        if (n >= threshold) resize();
        int h = hash(key);
        int initialHash = -1;
        while (initialHash != h && table[h] != null && !table[h].key.equals(key)) {
          if (initialHash == -1) {
            initialHash = h;
          }
          h = (h + 1) % m;
        }
        if (table[h] == null) {
          table[h] = new Entry<K, V>(key, value);
          n++;
          return;
        } else if (initialHash != h)
          if (table[h] != null && table[h].key.equals(key)) {
            table[h].value = value;
            n++;
            return;
          } else {
            table[h] = new Entry<K, V>(key, value);
            n++;
          }
        return;
      }

      public V remove(K key) {
        int h = hash(key);
        int initialHash = -1;

        if (table[h] == null)  {
          return null; // no key found
        }
        while (!table[h].key.equals(key)) {
          h = (h + 1) % m;
          if (table[h] == null || h == initialHash) {
            return null;
          }
        }

        V r = table[h].value;
        table[h] = null;
        while (table[++h] != null) {
          Entry<K, V> e = table[h];
          table[h] = null;
          n--;
          this.add(e.key, e.value);
        }
        n--;
        return r;
      }

      public boolean contains(K key) {
        int h = hash(key);
        int initialHash = h;

        if (table[h] == null) return false;
        while (!table[h].key.equals(key)) {
          h = (h + 1) % m;
          if (table[h] == null || h == initialHash)
            return false;
        }
        return true;
      }

      @SuppressWarnings("unchecked")
      public void clear() {
        table = (Entry<K, V>[])new Entry[m];
        n = 0;
      }

      public boolean isEmpty() {
        return n == 0;
      }

      public int getSize() {
        return n;
      }

      public ValuesIterator values() {
        return new ValuesIterator();
      }

      public KeysIterator keys() {
        return new KeysIterator();
      }

      class KeysIterator implements Iterator<K>, Iterable<K> {
        int nextIndex = 0;
        int numeroDeCosasQueYaPasaron = 0;

        public KeysIterator() {};

        public Iterator<K> iterator() {
          return this;
        }

        public K next() {
          while (this.hasNext()) {
            if (table[nextIndex++] != null) {
              numeroDeCosasQueYaPasaron++;
              return table[nextIndex-1].key;
            }
          }
          throw new NoSuchElementException();
        }

        public boolean hasNext() {
          return numeroDeCosasQueYaPasaron < n;
        }
      }

      class ValuesIterator implements Iterator<V>, Iterable<V> {
        int nextIndex = 0;
        int numeroDeCosasQueYaPasaron = 0;

        public ValuesIterator() {};

        public Iterator<V> iterator() {
          return this;
        }

        // TODO: incializar con indice

        public V next() {
          while (this.hasNext()) {
            if (table[nextIndex++] != null) {
              numeroDeCosasQueYaPasaron++;
              return table[nextIndex-1].value;
            }
          }
          throw new NoSuchElementException();
        }

        public boolean hasNext() {
          return numeroDeCosasQueYaPasaron < n;
        }
      }

      public static void main(String[] args) {
        OpenAddressingDict<String, String> dict = new OpenAddressingDict<>();
        dict.add("Lucio", "Pez");
        System.out.println(dict.getValue("Lucio"));
        System.out.println();
        dict.add("Pichón", "Pajaro");
        System.out.println();
        System.out.println(dict.getValue("Lucio"));
        dict.add("Popo", "cosas");
        dict.add("Vaso", "Vierre");
        System.out.println(dict.getValue("Lucio"));
        dict.add("Verde", "Vierre");
        dict.add("Pez", "Poisson");
        dict.add("Wololo", "AOfE");
        dict.add("qwerty", "asdfg");
        System.out.println();
        System.out.println(dict.hash("Pez"));
        System.out.println(dict.getValue("Lucio"));
        System.out.println(dict.getValue("Popo"));
        System.out.println(dict.getValue("Vaso"));
        System.out.println(dict.getValue("Wololo"));
        System.out.println(dict.threshold);
        System.out.println(dict.isEmpty());
        System.out.println(dict.contains("Verde"));
        System.out.println(dict.contains("Wololo"));
        System.out.println(dict.contains("asdsad"));
        System.out.println(dict);
        System.out.println(dict.remove("Pichón"));
        System.out.println(dict.remove("Lucio"));
        System.out.println(dict.remove("Vaso"));
        System.out.println(dict.remove("Wololo"));
        System.out.println(dict.remove("querty"));
        System.out.println(dict.remove("Popo"));
        System.out.println(dict.remove("Pez"));
        System.out.println(dict.remove("Verde"));
        System.out.println(dict.isEmpty());
        System.out.println(dict.remove("qwerty"));
        System.out.println(dict);
        dict.add("Lucio", "Pez");
        System.out.println(dict.getValue("Lucio"));
        System.out.println();
        dict.add("Pichón", "Pajaro");
        System.out.println();
        System.out.println(dict.getValue("Lucio"));
        dict.add("Popo", "cosas");
        dict.add("Vaso", "Vierre");

        System.out.println("Iterador: juejuejeue");
        Iterator<String> keys = dict.keys();
        Iterator<String> values = dict.values();

        while(keys.hasNext()) {
          System.out.println(keys.next());
          System.out.println(keys.hasNext());
        }
        while (values.hasNext()) {
          System.out.println(values.next());
          System.out.println(values.hasNext());
        }

        for (String key : dict.keys()) {
          System.out.println(key);
        }
      }

      public String toString() {
        StringBuilder sb = new StringBuilder("{ ");
        for (Entry<K, V> e : table) {
          if (e != null)
            sb.append(e.key).append(": ").append(e.value).append(", ");
        }
        return sb.append('}').toString();
      }

      private static class Entry<K, V> {
        K key;
        V value;

        Entry (K k, V v) {
          key = k;
          value = v;
        }
      }
}
