package structures.queue;

import structures.list.List;

@SuppressWarnings("rawtypes")
public interface Queue<V> {
  public boolean empty();
  public V front();
  public V rear();
  public V dequeue();
  public void enqueue(V object);
  public int size();

  @SuppressWarnings("unchecked")
  public static void radixSort(List<Integer> data, int k) {
    int radix = 10, power = 1, element, digit;
    Queue<Integer>[] digitQueue = new ArrayQueue[radix];
    for (int i = 0; i < digitQueue.length; i++) {
      digitQueue[i] = new ArrayQueue<Integer>(data.size());
    }

    for (int i = 0; i < k; i++) {
      int size = data.size();
      for (int j = 0; j < size; j++) {
        element = data.remove(0);
        digit = (element/power) % radix;
        digitQueue[digit].enqueue(element);
      }

      for (int j = 0; j < radix; j++) {
        while (!digitQueue[j].empty()) {
          data.add(digitQueue[j].dequeue());
        }
      }

      power *= radix;
    }
  }
}
