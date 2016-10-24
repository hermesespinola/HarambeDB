package tree;

import java.util.Stack;
import node.BinaryTreeNode;
import java.util.Iterator;

public class TreeIterator<K, V> implements Iterator<V> {
  Stack<BinaryTreeNode<K, V>> stack;

  public TreeIterator(BinaryTreeNode<K, V> root) {
    stack = new Stack<BinaryTreeNode<K, V>>();
    while (root != null) {
      stack.push(root);
      root = root.left();
    }
  }

  public boolean hasNext() {
    return !stack.isEmpty();
  }

  public V next() {
    BinaryTreeNode<K, V> node = stack.pop();
    V result = node.getValue();
    if (node.right() != null) {
      node = node.right();
      while (node != null) {
        stack.push(node);
        node = node.left();
      }
    }
    return result;
  }
}
