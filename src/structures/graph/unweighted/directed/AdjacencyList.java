package structures.graph.unweighted.directed;

import structures.graph.unweighted.UnweightedGraph;
import structures.list.ArrayLinearList;
import structures.list.LinkedList;
import structures.graph.Vertex;
import structures.graph.Graph;

public class AdjacencyList implements UnweightedGraph<ALVertex> {

  ArrayLinearList<ALVertex> matrix;
  private int vertexCount;
  private static final long serialVersionUID = 34l;

  public AdjacencyList(int vertexCount) {
    this.vertexCount = vertexCount;
    this.matrix = new ArrayLinearList<>();
    for (int i = 0; i < vertexCount; i++) {
      this.matrix.add(i, new ALVertex(i));
    }
  }

  public void addEdge(int from, int to) {
    int edgeToAdd = (from == vertexCount) ? from : (to == vertexCount) ? to : -1;
    if (edgeToAdd != -1) {
      this.addVertex();
    }
    if (from >= 0 && from <= vertexCount && to >= 0 && to <= vertexCount) {
      if (from == to) return;
      this.matrix.get(from).connectVertex(this.matrix.get(to));
    } else throw new IndexOutOfBoundsException();
  }

  public void addVertex() {
    this.matrix.add(vertexCount, new ALVertex(vertexCount++));
  }

  public void removeEdge(int from, int to) {
    if (from >= 0 && from < vertexCount && to > 0 && to < vertexCount) {
      if (from == to) return;
      this.matrix.get(from).removeAdjacentVertex(this.matrix.get(to));
    } else throw new IndexOutOfBoundsException();
  }

  public ALVertex getVertex(int vertex) {
    if (vertex >= 0 && vertex < vertexCount) {
      return this.matrix.get(vertex);
    } else throw new IndexOutOfBoundsException();
  }

  public int vertexCount() {
    return this.vertexCount;
  }

  public ALVertex[] getGraph() {
    ALVertex[] vertices = new ALVertex[this.matrix.size()];
    this.matrix.copyToArray(vertices);
    return vertices;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('{').append('\n');
    for (ALVertex v : matrix) {
      sb.append("  ").append(v).append('\n');
    }
    return sb.append('}').toString();
  }
}
