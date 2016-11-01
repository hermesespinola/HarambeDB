package structures.matrix;

public class ArrayMatrix<V> implements Matrix<V> {
  int numColumns, numRows;
  V[] arr;

  @SuppressWarnings("unchecked")
  public ArrayMatrix(int rows, int cols) {
    this.numRows = rows;
    this.numColumns = cols;
    this.arr = (V[])new Object[cols * rows];
  }

  public V get(int i, int j) {
    checkIndexes(i, j);
    return arr[i * numColumns + j];
  }

  public V remove(int i, int j) {
    checkIndexes(i, j);
    V val = arr[i * numColumns + j];
    arr[i * numColumns + j] = null;
    return val;
  }


  public void set(int i, int j, V value) {
    checkIndexes(i, j);
    arr[i * numColumns + j] = value;
  }

  public int numColumns() {
    return this.numColumns;
  }

  public int numRows() {
    return this.numRows;
  }

  private void checkIndexes(int i, int j) {
    if (i < 0 || j < 0 || i > numColumns || j > numRows)
    throw new IndexOutOfBoundsException();
  }
}
