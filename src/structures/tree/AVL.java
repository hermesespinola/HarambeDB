package structures.tree;

import java.util.Stack;

/*
* Implementation of an AVL tree extending a binary search tree with key value pairs.
*/
public class AVL<K extends Comparable<? super K>, V> extends BST<K, V> {
  private static final long serialVersionUID = 11L;
  protected int size;
  private static class AVLNode<K extends Comparable<? super K>, V> extends BSTNode<K, V> {
    protected int height;
    private static final long serialVersionUID = 15L;

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
  }

  public AVL() {
    super();
  }

  public AVL(K rootKey, V rootValue) {
    super(rootKey, rootValue);
  }

  /*
  * Recursively add a new node to the AVL tree.
  * @param key The key of the new Node.
  * @param val The value of the new node
  */
  @Override
  public void add(K key, V val) {
    this.root = add(key, val, (AVLNode<K,V>)root);
    this.size++;
  }

  /*
  * Recursively remove the node with the specified key and rebalance the tree.
  * @param key The key of the node to remove.
  */
  @Override
  public void remove(K key) {
    root = remove(key, root());
  }

  /*
  * The actual remove method
  * @param key The key of the node to remove.
  * @param node root of the where the child node is to be deleted.
  */
  @SuppressWarnings("unchecked")
  protected AVLNode<K,V> remove(K key, AVLNode<K,V> node) {
    if (node == null)
      return null;
    if (key.compareTo(node.getKey()) < 0) {

      node.setLeft(remove(key, node.left()));
      if (nodeHeight(node.right()) - nodeHeight(node.left()) >= 2) {
        int rightHeight = nodeHeight(node.right().right());
        int leftHeight = nodeHeight(node.right().left());

        if (rightHeight >= leftHeight) {
          node = singleLeftRotation(node);
        } else {
          node = doubleLeftRotation(node);
        }
      }

    } else if (key.compareTo(node.getKey()) > 0) {

      node.setRight(remove(key, node.right()));
      if (nodeHeight(node.left()) - nodeHeight(node.right()) >= 2) {
        int leftHeight = nodeHeight(node.left().left());
        int rightHeight = nodeHeight(node.left().right());

        if (leftHeight >= rightHeight) {
          node = singleRightRotation(node);
        } else {
          node = doubleRightRotation(node);
        }
      }

    } else if (node.left() != null) { // key == node.key (remove this node)
      BSTNode<K,V> greaterLeft = node.left().greater();
      node.setValue(greaterLeft.getValue());
      node.setKey(greaterLeft.getKey());

      node.setLeft(remove(greaterLeft.getKey(), node.left()));
      if (nodeHeight(node.right()) - nodeHeight(node.left()) >= 2) {
        int rightHeight = nodeHeight(node.right().right());
        int leftHeight = nodeHeight(node.right().left());
        if (rightHeight >= leftHeight) {
          node = singleLeftRotation(node);
        } else {
          node = doubleLeftRotation(node);
        }
      }
    } else {
      node = node.left() != null ? node.left() : node.right();
    }

    // update the current node height
    if (node != null)
      node.height = Math.max(nodeHeight(node.left()), nodeHeight(node.right())) + 1;

    return node;
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
    avl.add(84, "84");
    avl.add(10, "10");
    avl.add(8, "8");
    avl.add(92, "92");
    avl.add(66, "66");
    avl.add(88, "88");
    avl.add(29, "29");
    avl.add(27, "27");
    avl.add(75, "75");
    avl.add(72, "72");
    avl.add(68, "68");
    avl.add(62, "62");
    avl.add(18, "18");
    avl.add(80, "80");
    avl.add(36, "36");
    avl.add(1, "1");
    avl.add(40, "40");

    avl.remove(92);
    avl.remove(8);
    avl.remove(36);
    avl.remove(1);
    System.out.println(avl);
  }
}
