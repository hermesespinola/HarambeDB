package structures.matrix;

public interface Matrix<V>  {
  public V get(int i, int j);
  public V remove(int i, int j);
  public void set(int i, int j, V value);
  public int numColumns();
  public int numRows();
}
