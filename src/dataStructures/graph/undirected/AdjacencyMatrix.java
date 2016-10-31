import matrix.TriangularMatrix;
import list.List;
import list.ArrayLinearList;

public class AdjacencyMatrix implements Graph {
  TriangularMatrix<Boolean> matrix;
  private int vertexCount; // matrix.size = vertexCount * vertexCount

  public AdjacencyMatrix(int vertexCount) {
    this.vertexCount = vertexCount;
    this.matrix = new TriangularMatrix<Boolean>(vertexCount, false);
  }

  public void addEdge(int i, int j) {
    if (i >= 0 && i < vertexCount && j > 0 && j < vertexCount) {
      matrix.set(i, j, true);
    }
  }

  public void removeEdge(int i, int j) {
    if (i >= 0 && i < vertexCount && j > 0 && j < vertexCount) {
      matrix.remove(i, j);
    }
  }

  public List<Integer> getAdjecentEdges(int vertex) {
    if (vertex >= 0 && vertex < vertexCount) {
      List<Integer> adjacencyList = new ArrayLinearList<>(vertexCount-1);
      for (int j = 0; j < vertexCount; j++) {
        if (matrix.get(vertex, j)) {
          adjacencyList.add(j);
        }
      }
      return adjacencyList;
    } else throw new IndexOutOfBoundsException();
  }

  public String toString() {
    return matrix.toString();
  }

  public static void main(String[] args) {
    int vertexCount = 10;
    Graph graph = new AdjacencyMatrix(vertexCount);
    graph.addEdge(0,0); // no self-loops allowed
    for (int i = 0; i < vertexCount; i += 2) {
      for (int j = 1; j < vertexCount; j += 2) {
        graph.addEdge(i, j);
      }
    }
    System.out.println(graph);
  }
}
