package structures.graph.weighted.undirected;

import structures.graph.weighted.WeightedGraph;
import structures.list.ArrayLinearList;
import structures.list.LinkedList;
import structures.graph.Vertex;
import structures.graph.Graph;
import structures.graph.Edge;

public class AdjacencyList implements WeightedGraph<ALVertex> {
  ALVertex[] matrix;
  private int vertexCount;

  public AdjacencyList(int vertexCount) {
    this.vertexCount = vertexCount;
    this.matrix = new ALVertex[vertexCount];
    for (int i = 0; i < vertexCount; i++) {
      this.matrix[i] = new ALVertex(i);
    }
  }

  public void addEdge(int i, int j, float weight) {
    if (i >= 0 && i < vertexCount && j >= 0 && j < vertexCount) {
      if (i == j) return;
      ALVertex v1 = matrix[i];
      ALVertex v2 = matrix[2];
      this.matrix[i].connectVertex(this.matrix[j], weight);
      this.matrix[j].connectVertex(this.matrix[i], weight);
    } else throw new IndexOutOfBoundsException();
  }

  public void removeEdge(int i, int j) {
    if (i >= 0 && i < vertexCount && j > 0 && j < vertexCount) {
      if (i == j) return;
      this.matrix[i].removeEdge(this.matrix[j]);
      this.matrix[j].removeEdge(this.matrix[i]);
    } else throw new IndexOutOfBoundsException();
  }

  public ALVertex getVertex(int vertex) {
    if (vertex >= 0 && vertex < vertexCount) {
      return this.matrix[vertex];
    } else throw new IndexOutOfBoundsException();
  }

  public int vertexCount() {
    return this.vertexCount;
  }

  public ALVertex[] getGraph() {
    return this.matrix;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('{').append('\n');
    for (ALVertex v : matrix) {
      sb.append("  ").append(v).append('\n');
    }
    return sb.append('}').toString();
  }

  public static void main(String[] args) {
    WeightedGraph<ALVertex> g = new AdjacencyList(10);
    g.addEdge(1, 4, 2);
    g.addEdge(1, 6, 4.65f);
    g.addEdge(4, 6, 14.5f);
    g.addEdge(0, 9, 7.45f);
    g.addEdge(4, 9, 23.9f);
    g.addEdge(8, 3, 10.3f);
    g.addEdge(7, 2, 9.4f);
    g.addEdge(6, 5, 2.3f);
    System.out.println(g);
    Graph.BFS(g, 4);
    System.out.println(g);
    Graph.DFS(g, 4);
    System.out.println(g);
  }
}

class ALVertex extends Vertex<ALVertex> {
  LinkedList<Edge<ALVertex>> outgoingEdges;

  public ALVertex(int value) {
    this.value = value;
    this.outgoingEdges = new LinkedList<>();
  }

  public ALVertex(int value, LinkedList<Edge<ALVertex>> outgoingEdges) {
    this.value = value;
    this.outgoingEdges = outgoingEdges;
  }

  public void connectVertex(ALVertex other, float weight) {
    this.outgoingEdges.add(0, new Edge<ALVertex>(other, weight));
  }

  public void removeEdge(ALVertex v) {
    this.outgoingEdges.remove(outgoingEdges.indexOf(new Edge<ALVertex>(v, -1))); // the weight does not matter
  }

  public ArrayLinearList<ALVertex> adjacentVertices() {
    ArrayLinearList<ALVertex> vertices = new ArrayLinearList<>(this.outgoingEdges.size());
    for (Edge<ALVertex> e : outgoingEdges) {
      vertices.add(e.vertex());
    }
    return vertices;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder().append(this.value).append(": [");
    for (Edge<ALVertex> e : outgoingEdges) {
      sb.append(e.vertex().getValue()).append(": ").append(e.weight()).append(", ");
    }
    if (sb.length() > 2) sb.setLength(sb.length() - 2);
    return sb.append(']').toString();
  }

  @Override
  public boolean equals(Object other) {
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof ALVertex)) return false;
    return this.value == ((ALVertex)other).value;
  }

  @Override
  public int hashCode() {
    return ((Integer)this.value).hashCode();
  }
}
