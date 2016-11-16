package structures.queue;

public class ArrayQueue<T> implements Queue<T> {
  private int size;
  private CircularArray<T> arr;
  private int nextEnqueueIndex;
  private int nextDequeueIndex;

  public ArrayQueue() {
    this(100);
  }

  public ArrayQueue(int capacity) {
    this.size = 0;
    this.arr = new CircularArray<T>(capacity);
    this.nextEnqueueIndex = capacity;
    this.nextDequeueIndex = capacity;
  }

  public int size() {
    return this.size;
  }

  public boolean empty() {
    return nextDequeueIndex == nextEnqueueIndex;
  }

  public void enqueue(T element) {
    if (this.size == this.arr.length()) {
      this.resize();
     }
     this.arr.set(nextEnqueueIndex, element);
     nextEnqueueIndex--;
     size++;
   }

   public T front() {
     return this.arr.get(nextDequeueIndex);
   }

  public T rear() {
    return this.arr.get(nextEnqueueIndex+1);
  }

   public T dequeue() {
     if (this.empty()) return null;
     T x = this.arr.remove(nextDequeueIndex);
     nextDequeueIndex--;
     size--;
     return x;
   }

   private void resize() {
     CircularArray<T> newArr = new CircularArray<T>(this.size*2);
     for (int i = nextDequeueIndex, j = newArr.length(); i > nextEnqueueIndex; i--, j--) {
      newArr.set(j, this.arr.get(i));
    }
    this.arr = newArr;
    nextDequeueIndex = this.arr.length();
    nextEnqueueIndex = this.arr.length()-this.size;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    for (int i = nextDequeueIndex; i > nextEnqueueIndex; i--) {
      sb.append("[ "); sb.append(this.arr.get(i)); sb.append(" ]");
    }
    sb.append(']');
    return sb.toString();
  }

  public static class CircularArray<T> {
    T[] arr;
    final int size;
    private static int modulus(int x, int mod) {
      return ((x % mod) + mod) % mod;
    }

    @SuppressWarnings("unchecked")
    public CircularArray(int size) {
      this.arr = (T[])new Object[size+1];
      this.size = this.arr.length;
    }

    public int length() {
      return this.size-1;
    }

    public int size() {
      return this.size;
    }

    public T get(int index) {
      return arr[modulus(index,this.size)];
    }

    public void set(int index, T element) {
      this.arr[modulus(index,this.size)] = element;
    }

    public T remove(int index) {
      T ret = this.arr[modulus(index,this.size)];
      this.arr[modulus(index,this.size)] = null;
      return ret;
    }

    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append('[');
      for (T el : this.arr) {
        sb.append("[ ");sb.append(el);sb.append(" ]");
      } sb.append("...]");
      return sb.toString();
    }
  }

}
