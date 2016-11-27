package structures.dict;

import java.util.Iterator;
import java.io.Serializable;
import java.util.List;

/**
* List of pairs.
*
* Dictionary is a data structure which allows to save data in a key-value format. This is an alternative
* to linear data structures, such as array or vector, in which a numeric key is necessary. Instead, any
* type of class might be used as a key.
* 
* <p>Dictionary complexity tends to 1, but it gets bigger as the array begins to fill. In order to retrieve good complexity,
* a resize of the array is done whenever the number of pairs against the array capacity overflows a factor.
* 
* <p>This class is a member of the
* <a href="{@docRoot}/../index.html">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
* @see     Column
*/
public interface Dict<K,V> extends Serializable  {
  /**
   * Adds a pair of key-value to the list. The key gets hashed and properly added into position. If the given key already exists,
   * the previous value gets overrided with the new one.
   * 
   * @param key key of the pair
   * @param value value of the pair
   */
  public void add(K key, V value);
  
  /**
   * Removes a pair of key-value with given key and returns the previous value. If the given key does not exist, 
   * it returns null. Removin the value also removes the key and decrements the size.
   * 
   * @param key key of the pair to be removed
   * @return previous value of the pair or null if pair does not exist
   */
  public V remove(K key);
  
  /**
   * Hashes the key and returns the corresponding value. If said value does not exists, it returns null.
   * 
   * @param key key of the pair to be found
   * @return value of the given pair
   */
  public V getValue(K key);
  
  /**
   * Checks if the dictionary contains a pair with given key. It returns true if there exists a pair with given key,
   * false if not.
   * 
   * @param key key for the pair to be found
   * @return true if key exists, false if not
   */
  public boolean contains(K key);
  
  /**
   * Returns an iterable object of all the keys. This is to be used in a for-each loop.
   * 
   * @return Iterable of the keys
   */
  public Iterable<K> keys();
  
  /**
   * Returns an iterable object of all the values. This is to be used in a for-each loop.
   * 
   * @return Iterable of the values
   */
  public Iterable<V> values();
  
  /**
   * Returns if the dictionary contains no key-value pairs.
   * 
   * @return true if the dictionary is empty, false if not
   */
  public boolean isEmpty();
  
  /**
   * Returns the number of key-value pairs contained in the dictionary.
   * 
   * @return number of pairs stored
   */
  public int getSize();
  
  /**
   * Deletes all key-value pairs from the dictionary.
   */
  public void clear();
}
