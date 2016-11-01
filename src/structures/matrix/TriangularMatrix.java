package structures.matrix;

public class TriangularMatrix<V> implements Matrix<V> {
  private final V[] matrix;
  private final V zero;
  private final int size;

  @SuppressWarnings("unchecked")
  public TriangularMatrix(int size, V zeroValue) {
    this.matrix = (V[]) new Object[(size * size + size) / 2];
    for (int i = 0; i < matrix.length; i++) {
      matrix[i] = zeroValue;
    }
    this.zero = zeroValue;
    this.size = size;
  }

  private static final int getIndex(final int i, final int j) {
    if (i < j)
      return j * (j + 1) / 2 + i;
    else
      return i * (i + 1) / 2 + j;
  }

  public V get(int i, int j) {
    if (i >= 0 && j >= 0 && i < size && j < size) {
      return matrix[getIndex(i,j)];
    } else throw new IndexOutOfBoundsException();
  }

  public V remove(int i, int j) {
    if (i >= 0 && j >= 0 && i < size && j < size) {
      V val = matrix[getIndex(i, j)];
      matrix[getIndex(i, j)] = zero;
      return val;
    } else throw new IndexOutOfBoundsException();
  }

  public void set(int i, int j, V value) {
    if (i >= 0 && j >= 0 && i < size && j < size) {
      matrix[getIndex(i,j)] = value;
    } else throw new IndexOutOfBoundsException();
  }

  public int numColumns() {
    return this.size;
  }

  public int numRows() {
    return this.size;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        sb.append(get(i, j)).append(' ');
      }
      sb.append('\n');
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    int size = 10;
    TriangularMatrix<Integer> matrix = new TriangularMatrix<Integer>(size, 0);
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        matrix.set(i, j, i + j);
      }
    }
    System.out.println(matrix);
  }
}
