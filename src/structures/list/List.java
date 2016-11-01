package structures.list;

// TODO: make iterable
public interface List<T> extends Iterable<T> {
  public boolean empty();
  public int size();
  public T get(int index);
  public int indexOf(T x);
  public T remove(int index);
  public void add(int index, T element);
  public void add(T element);
}
