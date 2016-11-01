package structures.matrix;

public class DiagonalMatrix<V> implements Matrix<V> {
  V zero;
  V matrix[];
  int size;

  @SuppressWarnings("unchecked")
  public DiagonalMatrix(V zeroElement, int size) {
    matrix = (V[]) new Object[size];
    this.zero = zeroElement;
    this.size = size;
  }

  public V get(int i, int j) {
    if (i == j) {
      return matrix[i];
    } else {
      return zero;
    }
  }

  public V remove(int i, int j) {
    if (i == j) {
      V val = matrix[i];
      matrix[i] = zero;
      return val;
    } else {
      return zero;
    }
  }

  public void set(int i, int j, V value) {
    if (i == j) {
      matrix[i] = value;
    }
  }

  public int numColumns() {
    return size;
  }

  public int numRows() {
    return size;
  }
}
