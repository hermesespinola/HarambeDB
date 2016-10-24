package heap;

import java.lang.RuntimeException;

@SuppressWarnings("rawtypes")
public class BinaryHeap<V extends Comparable<V>> implements Heap<V> {

  private HeapType heapType;
  private int size;
  public static final int DEF_SIZE = 100;
  private V[] heap;
  private int length;

  public BinaryHeap(HeapType heapType) {
    this(DEF_SIZE, heapType);
  }

  @SuppressWarnings("unchecked")
  public BinaryHeap(int initialSize, HeapType heapType) {
    this.heapType = heapType;
    this.heap = (V[]) new Comparable[initialSize];
    this.length = 0;
  }

  public BinaryHeap(V[] values, HeapType heapType) {
    this(DEF_SIZE + values.length, heapType);
    for (V v : values) {
      this.insert(v);
    }
  }

  protected void heapifyUp(int i) {
    if (heapType == HeapType.MaxHeap)
      maxHeapifyUp(i);
    else
      minHeapifyUp(i);
  }

  private void maxHeapifyUp(int i) {
    do {
      int parent = (i+1)/2 - 1;
      int cmp = this.heap[i].compareTo(this.heap[parent]);
      if (cmp > 0)
        shift(this.heap, i, parent);
      else
        break;
      i = parent;
    } while (i > 0);
  }

  private void minHeapifyUp(int i) {
    do {
      int parent = i/2;
      int cmp = this.heap[i].compareTo(this.heap[parent]);
      if (cmp < 0)
        shift(this.heap, i, parent);
      else
        break;
      i = parent;
    } while (i > 0);
  }

  protected void heapifyDown(int i) {
    if (i < 0 || i >= this.length) throw new IndexOutOfBoundsException();
    if (heapType == HeapType.MaxHeap)
      maxHeapifyDown(i);
    else
      minHeapifyDown(i);
  }

  private void maxHeapifyDown(int i) {
    while (i < this.length/2) {
      int left = (i + 1)*2 - 1;
      int right = left + 1;
      int path = left;
      if (heap[right] != null && this.heap[right].compareTo(this.heap[left]) > 0)
        path = right;
      if (this.heap[path].compareTo(this.heap[i]) > 0) {
        shift(this.heap, i, path);
      } else break;
      i = path;
    }
  }

  private void minHeapifyDown(int i) {
    while (i < this.length/2) {
      int left = (i + 1)*2 - 1;
      int right = left + 1;
      int path = left;
      if (heap[right] != null && this.heap[right].compareTo(this.heap[left]) < 0)
        path = right;
      if (this.heap[path].compareTo(this.heap[i]) < 0) {
        shift(this.heap, i, path);
      } else break;
      i = path;
    }
  }

  private static final <V> void shift(V[] heap, int i, int j) {
    V tmp = heap[i];
    heap[i] = heap[j];
    heap[j] = tmp;
  }

  @SuppressWarnings("unchecked")
  private void resize() {
    V[] newHeap = (V[]) new Comparable[heap.length * 2];
    System.arraycopy(this.heap, 0, newHeap, 0, this.heap.length);
    this.heap = newHeap;
  }

  public void insert(V value) {
    if (this.isFull())
      this.resize();
    this.heap[this.length] = value;
    if (this.length > 0)
      heapifyUp(this.length);
    this.length++;
  }

  @SuppressWarnings("unchecked")
  public void clear() {
    this.heap = (V[]) new Comparable[this.heap.length];
  }

  public boolean isEmpty() {
    return this.length == 0;
  }

  public boolean isFull() {
    return this.length == this.heap.length;
  }

  public V peek() {
    return this.heap[0];
  }

  public V pop() {
    if (this.length <= 0)
      throw new RuntimeException("Empty Heap");
    this.length--;
    V r = this.heap[0];
    this.heap[0] = this.heap[length];
    this.heap[this.length] = null;
    if (this.length > 1) {
      heapifyDown(0);
    }
    return r;
  }

  public int size() {
    return this.length - 1;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder("[ ");
    for (int i = 0; i < this.length; i++) {
      sb.append(heap[i]).append(' ');
    } sb.append(']');
    return sb.toString();
  }

  public enum HeapType {
    MaxHeap,
    MinHeap
  }

  public static void main(String[] args) {
    BinaryHeap<Integer> heap = new BinaryHeap<Integer>(new Integer[] {3, 4, 7, 3, 5, 6, 8, 9, 2, 1, 7, 10}, HeapType.MinHeap);
    System.out.println(heap);
    while (!heap.isEmpty()) {
      System.out.println(heap.pop());
    }
  }
}
