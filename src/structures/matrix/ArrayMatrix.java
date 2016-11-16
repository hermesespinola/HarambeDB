package structures.matrix;

public class ArrayMatrix<V> implements Matrix<V> {
  int numColumns, numRows;
  V[] arr;
  V zero;

  @SuppressWarnings("unchecked")
  public ArrayMatrix(int rows, int cols, V zero) {
    this.numRows = rows;
    this.numColumns = cols;
    this.arr = (V[])new Object[cols * rows];
    this.zero = zero;
    for (int i = 0; i < this.arr.length; i++) {
      this.arr[i] = zero;
    }
  }

  public V get(int i, int j) {
    checkIndexes(i, j);
    return (arr[i * numColumns + j] != null) ? arr[i * numColumns + j] : zero;
  }

  public V remove(int i, int j) {
    checkIndexes(i, j);
    V val = arr[i * numColumns + j];
    arr[i * numColumns + j] = zero;
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
