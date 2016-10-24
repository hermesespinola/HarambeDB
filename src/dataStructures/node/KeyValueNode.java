package node;

public class KeyValueNode<K, V> extends Node<V> {
  protected K key;

  public KeyValueNode(K key, V val) {
    super(val);
    this.key = key;
  }

  public void setKey(K key) {
    this.key = key;
  }

  public K getKey() {
    return this.key;
  }
}
