package structures.graph.weighted.directed;

import structures.graph.weighted.WeightedGraph;
import structures.list.ArrayLinearList;
import structures.matrix.ArrayMatrix;
import structures.graph.Vertex;
import structures.list.List;

public class AdjacencyMatrix implements WeightedGraph<AMVertex> {
  ArrayMatrix<Float> matrix;
  AMVertex[] vertices;
  private int vertexCount;

  public AdjacencyMatrix(int vertexCount) {
    this.vertexCount = vertexCount;
    this.matrix = new ArrayMatrix<Float>(vertexCount, vertexCount, 0f);
    this.vertices = new AMVertex[vertexCount];
    for (int i = 0; i < vertexCount; i++) {
      vertices[i] = new AMVertex(i, this);
    }
  }

  public void addEdge(int i, int j, float weight) {
    if (i >= 0 && i < vertexCount && j >= 0 && j < vertexCount) {
      matrix.set(i, j, weight);
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  public void removeEdge(int i, int j) {
    if (i >= 0 && i < vertexCount && j > 0 && j < vertexCount) {
      matrix.remove(i, j);
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  public String toString() {
    StringBuilder sb = new StringBuilder().append(" |");
    for (int i = 0; i < matrix.numRows(); i++) {
      sb.append(i).append('|');
    }
    sb.append('\n');
    for (int i = 0; i < matrix.numRows(); i++) {
      sb.append(i).append('|');
      for (int j = 0; j < matrix.numColumns(); j++) {
        sb.append(matrix.get(i, j)).append(' ');
      }
      sb.append('\n');
    }
    return sb.toString();
  }

  public int vertexCount() {
    return this.vertexCount;
  }

  public AMVertex getVertex(int vertex) {
    return this.vertices[vertex];
  }

  public float weightOfEdge(int from, int to) {
    return matrix.get(from, to);
  }

  public AMVertex[] getGraph() {
    return this.vertices;
  }

  public static void main(String[] args) {
    // int vertexCount = 10;
    // Graph<AMVertex> graph = new AdjacencyMatrix(vertexCount);
    // graph.addEdge(0,0); // no self-loops allowed
    // for (int i = 0; i < vertexCount; i += 2) {
    //   for (int j = 1; j < vertexCount; j += 2) {
    //     graph.addEdge(i, j);
    //   }
    // }
    // System.out.println(graph);
    // Graph.BFS(graph, 3);
    // System.out.println();
    // Graph.DFS(graph, 3);
  }
}

class AMVertex extends Vertex<AMVertex> {
  AdjacencyMatrix graph;

  public AMVertex(int value, AdjacencyMatrix graph) {
    this.value = value;
    if (graph == null) {
      throw new NullPointerException("null graph");
    }
    this.graph = graph;
  }

  // get the weight of the edge that connects this vertex with v.
  public float EdgeWeight(int v) {
    return graph.matrix.get(this.value, v);
  }

  public List<AMVertex> adjacentVertices() {
    List<AMVertex> adjacencyList = new ArrayLinearList<>(graph.vertexCount() - 1);
    for (int j = 0; j < graph.vertexCount(); j++) {
      if (graph.matrix.get(this.value, j) != 0) {
        adjacencyList.add(graph.getVertex(j));
      }
    }
    return adjacencyList;
  }

  public String toString() {
    return Integer.toString(this.value);
  }

  public boolean equals(Object other) {
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof AMVertex)) return false;
    return this.value == ((AMVertex)other).value;
  }

  @Override
  public int hashCode() {
    return ((Integer)this.value).hashCode();
  }
}
