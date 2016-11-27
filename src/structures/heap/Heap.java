package structures.heap;

import java.util.Iterator;

/**
* Complete binary tree in array representation
*
* <p>Heap is a data structure which reassembles a complete binary tree. It has the property that every node is bigger/smaller
* (depending on the type of heap) than its parent.
*
* <p>This class is a member of the
* <a href="{@docRoot}/../index.html" target="_top">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
* @see     Column
*/
public interface Heap <V> {
  /**
   * Empties the array and deletes all values.
   */
  public void clear();

  /**
   * Adds a given element in the end of the array and recursively, compares with its parent and goes higher if bigger/smaller.
   *
   * @param value element to add into the end of the heap
   */
  public void insert(V value);

  /**
   * Returns if the array is empty or not, this is, if the heap has any items.
   *
   * @return true if empty heap, false if not
   */
  public boolean isEmpty();

  /**
   * Returns if the array is full or not, this is, if the heap has no space.
   *
   * @return true if full heap, false if not
   */
  public boolean isFull();

  /**
   * Returns the root element of the heap, which is the biggest/smallest, depending on type of heap.
   *
   * @return top element in the heap
   */
  public V peek();

  /**
   * Returns the root element of the heap, replacing it with a null value and switching it to the bottom.
   *
   * @return top element in the heap
   */
  public V pop();

  /**
   * Returns the number of elements stored in the heap.
   * @return number of elements in the heap.
   */
  public int size();
}
