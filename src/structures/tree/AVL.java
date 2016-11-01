package structures.tree;

import java.util.Stack;

/*
* Implementation of an AVL tree extending a binary search tree with key value pairs.
*/
public class AVL<K extends Comparable<? super K>, V> extends BST<K, V> {
  private static class AVLNode<K extends Comparable<? super K>, V> extends BSTNode<K, V> {
    protected int height;

    protected AVLNode(K key, V value, AVLNode<K,V> leftNode, AVLNode<K,V> rightNode) {
      super(key, value, leftNode, rightNode);
    }

    protected AVLNode(K key, V value) {
      this(key, value, null, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AVLNode<K,V> left() {
      return (AVLNode<K,V>)super.left();
    }

    @Override
    @SuppressWarnings("unchecked")
    public AVLNode<K,V> right() {
      return (AVLNode<K,V>)super.right();
    }

    @Override
    public V remove(K other) {
      V r = super.remove(key);
      this.height = Math.max(nodeHeight(this.left()), nodeHeight(this.right())) + 1;
      return r;
    }
  }

  public AVL() {
    super();
  }

  public AVL(K rootKey, V rootValue) {
    super(rootKey, rootValue);
  }

  /*
  * iterative version of method add
  * @param key The key of the new Node.
  * @param val The value of the new node
  */
  @Override
  public void put(K key, V val) {
    Stack<AVLNode<K,V>> path = new Stack<>();
    path.push((AVLNode<K,V>)root);
    AVLNode<K, V> current = (AVLNode<K,V>)root;
    while (!path.empty()) {
      if (current == null)
        current = new AVLNode<K,V>(key, val);
      else if (key.compareTo(current.getKey()) < 0) {
        path.push(current.left());
        if (nodeHeight(current.left()) - nodeHeight(current.right()) == 2)
          if (key.compareTo(current.left().getKey()) < 0)
            current = singleRightRotation(current);
          else
            current = doubleRightRotation(current);

      } else if (key.compareTo(current.getKey()) > 0) {

        path.push(current.right());
        if (nodeHeight(current.right()) - nodeHeight(current.left()) == 2)
          if (key.compareTo(current.right().getKey()) < 0)
            current = singleLeftRotation(current);
          else
            current = doubleLeftRotation(current);

      } else {
        current.setValue(val);
      }
      current.height = Math.max(nodeHeight(current.left()), nodeHeight(current.right())) + 1;
      current = path.pop();
    }
  }

  /*
  * Recursively add a new node to the AVL tree.
  * @param key The key of the new Node.
  * @param val The value of the new node
  */
  @Override
  public void add(K key, V val) {
    this.root = add(key, val, (AVLNode<K,V>)root);
  }

  @Override
  public V remove(K key) {
    return root().remove(key);
  }

  /*
  * The actual add method.
  * @param key The key of the new Node.
  * @param node root of the where the node is to be added.
  * @param val The value of the new node.
  * @return the new root of the tree.
  */
  protected AVLNode<K,V> add(K key, V val, AVLNode<K,V> node) {
    if (node == null)
      node = new AVLNode<K,V>(key, val);
    else if (key.compareTo(node.getKey()) < 0) {

      node.setLeft(add(key, val, node.left()));
      if (nodeHeight(node.left()) - nodeHeight(node.right()) == 2)
        if (key.compareTo(node.left().getKey()) < 0)
          node = singleRightRotation(node);
        else
          node = doubleRightRotation(node);

    } else if (key.compareTo(node.getKey()) > 0) {

      node.setRight(add(key, val, node.right()));
      if (nodeHeight(node.right()) - nodeHeight(node.left()) == 2)
        if (key.compareTo(node.right().getKey()) > 0)
          node = singleLeftRotation(node);
        else
          node = doubleLeftRotation(node);

    } else node.setValue(val);

    node.height = Math.max(nodeHeight(node.left()), nodeHeight(node.right())) + 1;
    return node;
  }

  @SuppressWarnings("rawtypes")
  protected static int nodeHeight(AVLNode node) {
    return node != null ? node.height : - 1;
  }

  /*
  * Perform a single rotation on a binary tree node with its left child
  * and update heights.
  * @param x Root of the subtree to rotate.
  * @return the new root of the subtree.
  */
  private AVLNode<K,V> singleRightRotation(AVLNode<K,V> x) {
    AVLNode<K,V> newRoot = x.left();
    x.setLeft(newRoot.right());
    newRoot.setRight(x);
    x.height = Math.max(nodeHeight(x.left()), nodeHeight(x.right())) + 1;
    newRoot.height = Math.max(nodeHeight(newRoot.left()), nodeHeight(newRoot.right())) + 1;
    return newRoot;
  }

  /*
  * Perform a single rotation on a binary tree node with its right child
  * and update heights.
  * @param x Root of the subtree to rotate.
  * @return the new root of the subtree.
  */
  private AVLNode<K,V> singleLeftRotation(AVLNode<K,V> x) {
    AVLNode<K,V> newRoot = x.right();
    x.setRight(newRoot.left());
    newRoot.setLeft(x);
    x.height = Math.max(nodeHeight(x.left()), nodeHeight(x.right())) + 1;
    newRoot.height = Math.max(nodeHeight(newRoot.left()), nodeHeight(newRoot.right())) + 1;
    return newRoot;
  }

  /*
  * Perform a double rotation on a binary tree node with its left child
  * and update heights.
  * @param x Root of the subtree to rotate.
  * @return the new root of the subtree.
  */
  private AVLNode<K,V> doubleRightRotation(AVLNode<K,V> x) {
    x.setLeft(singleLeftRotation(x.left()));
    return singleRightRotation(x);
  }

  /*
  * Perform a double rotation on a binary tree node with its left child
  * and update heights.
  * @param x Root of the subtree to rotate.
  * @return the new root of the subtree.
  */
  private AVLNode<K,V> doubleLeftRotation(AVLNode<K,V> x) {
    x.setRight(singleRightRotation(x.right()));
    return singleLeftRotation(x);
  }

  public AVLNode<K,V> root() {
    return (AVLNode<K,V>)root;
  }

  public static void main(String[] args) {
    AVL<Integer, String> avl = new AVL<>();
    avl.add(2, "Dos");
    System.out.println(avl);
    System.out.println(avl.levelOrder());
    System.out.println();
    avl.add(1, "Uno");
    System.out.println(avl);
    System.out.println(avl.levelOrder());
    System.out.println();
    avl.add(4, "Cuatro");
    System.out.println(avl);
    System.out.println(avl.levelOrder());
    System.out.println();
    avl.add(3, "Tres");
    System.out.println(avl);
    System.out.println(avl.levelOrder());
    System.out.println();
    avl.add(6, "Seis");
    System.out.println(avl);
    System.out.println(avl.levelOrder());
    System.out.println();
    avl.add(5, "Cinco");
    System.out.println(avl);
    System.out.println(avl.levelOrder());
    System.out.println();
    avl.add(-2, "-Dos");
    System.out.println(avl);
    System.out.println(avl.levelOrder());
    System.out.println();
    avl.add(-4, "-Cuatro");
    System.out.println(avl);
    System.out.println(avl.levelOrder());
    System.out.println();
    avl.add(-3, "-Tres");
    System.out.println(avl);
    System.out.println(avl.levelOrder());
    System.out.println();
    avl.add(-1, "-Uno");
    System.out.println(avl);
    System.out.println(avl.levelOrder());
    System.out.println();
    avl.add(0, "Cero");
    System.out.println(avl);
    System.out.println(avl.levelOrder());
    System.out.println(avl.remove(-2));
    System.out.println(avl);
    System.out.println(avl.levelOrder());
  }
}
