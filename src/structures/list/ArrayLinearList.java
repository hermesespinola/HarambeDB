package structures.list;

import java.util.NoSuchElementException;
import java.util.ListIterator;

@SuppressWarnings("unchecked")
public class ArrayLinearList<T> implements List<T> {
private T[] arr;
private int size;
public static final int DEFAULT_SIZE = 100;
private static final long serialVersionUID = 20l;

public ArrayLinearList(int initialCapacity) throws IllegalArgumentException {
        if (initialCapacity < 1) {
                throw new IllegalArgumentException();
        }
        this.arr = (T[]) new Object[initialCapacity];
        this.size = 0;
}

public ArrayLinearList(int initialCapacity, int size) throws IllegalArgumentException {
        if (initialCapacity < 1) {
                throw new IllegalArgumentException();
        }
        this.arr = (T[]) new Object[initialCapacity];
        this.size = size;
}

public ArrayLinearList(T[] _arr) {
        this.arr = (T[]) new Object[DEFAULT_SIZE];
        System.arraycopy(_arr, 0, this.arr, 0, _arr.length);
        this.size = _arr.length;
}

public ArrayLinearList() {
        this(DEFAULT_SIZE);
}

@Override
public boolean empty() {
        return this.size == 0;
}

@Override
public int size() {
        return this.size;
}

@Override
public T get(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= this.size) throw new IndexOutOfBoundsException();
        return this.arr[index];
}

@Override
public int indexOf(T x) {
        for (int i = 0; i < this.size; i++) {
                if (this.arr[i].equals(x))
                        return i;
        }
        return -1;
}

public void set(int index, T newE) {
  if (index < 0 || index >= this.size) throw new IndexOutOfBoundsException();
  this.arr[index] = newE;
}

@Override
public T remove(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= this.size) throw new IndexOutOfBoundsException();
        T r = this.arr[index];
        for (int i = index + 1; i < this.size; i++) {
                this.arr[i - 1] = this.arr[i];
        }
        this.arr[--this.size] = null;
        return r;
};

private void resize() {
        T[] newArr = (T[]) new Object[this.size * 2];
        System.arraycopy(this.arr, 0, newArr, 0, this.arr.length);
        this.arr = newArr;
}

public void add(T newE) {
        this.add(this.size, newE);
}

@Override
public void add(int index, T newE) throws IndexOutOfBoundsException {
        if (index < 0 || index > this.size) throw new IndexOutOfBoundsException();

        if (this.size == this.arr.length) {
                this.resize();
        }

        for (int i  = this.size - 1; i >= index; i--)
                this.arr[i + 1] = this.arr[i];
        this.arr[index] = newE;
        this.size += 1;
}

public void RightShift(int amount) {
        if (this.size == this.arr.length + amount)
                this.resize();

        for (int i  = this.size; i >= 0; i--)
                this.arr[i + amount] = this.arr[i];
        for (int j = 0; j < amount; j++)
                this.arr[j] = null;
        this.size += amount;
}

@Override
public String toString() {
  StringBuilder sb = new StringBuilder("[ ");
        for (int i = 0; i < this.size; i++) {
                sb.append(arr[i]).append(' ');
        }
        sb.append(']');
  return sb.toString();
}

public ListIterator<T> iterator() {
        return new IteratorArray();
}

public ListIterator<T> iterator(int index) {
        return new IteratorArray(index);
}

public static void main(String[] args) {
        ArrayLinearList<String> list = new ArrayLinearList<String>();
	list.add("jamon");
	list.add(0, "queso");
        list.add(1, "lucio");
        list.add("qwe");
	list.add(0, "fds");
        list.add(1, "uuyt");
        list.add("barro");
	list.add(0, "trapeo");

        ListIterator<String> iter = list.iterator(3);
        while(iter.hasNext()) {
                System.out.println(iter.next());
        }
        System.out.println();
        while(iter.hasPrevious()) {
                System.out.println(iter.previous());
        }
}

class IteratorArray implements ListIterator<T> {
        private T next;
        private int nextIndex;

        public IteratorArray() {
                this.next = arr[0];
                this.nextIndex = 0;
        }
        public IteratorArray(int index) {
                this.next = arr[index];
                this.nextIndex = index;
        }

        public void add(T el){
                this.add(el);
        };
        public void set(T el){};
        public void remove(T el){};
        public void remove(){};
        public int previousIndex() {
                return this.nextIndex - 1;
        }

        public int nextIndex() {
                return this.nextIndex;
        }

        public T previous() {
                if (!this.hasPrevious())
                        throw new NoSuchElementException();

                this.nextIndex--;
                this.next = arr[this.nextIndex];
                return arr[nextIndex];
        }

        public T next() {
                if (this.hasNext()) {
                        this.nextIndex++;
                        this.next = arr[nextIndex];
                        return arr[nextIndex-1];
                }
                else throw new NoSuchElementException();
        }

        public boolean hasNext() {
                return this.next != null;
        }

        public boolean hasPrevious() {
                return this.nextIndex > 0;
        }
}
}
