import list.List;

public interface Graph {
  public void addEdge(int i, int j);
  public void removeEdge(int i, int j);
  public List<Integer> getAdjecentEdges(int vertex);
}
