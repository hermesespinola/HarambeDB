package structures.graph.unweighted;

import structures.graph.Vertex;
import structures.graph.Graph;

@SuppressWarnings("unchecked")
public interface UnweightedGraph <V extends Vertex<V>> extends Graph<V> {
  public void addEdge(int i, int j);
}
