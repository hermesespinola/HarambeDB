import list.ArrayLinearList;
import list.LinkedList;
import list.List;

@SuppressWarnings("rawtypes")
public class AdjacencyList implements Graph {
  LinkedList<Integer>[] matrix;
  private int vertexCount;

  @SuppressWarnings("unchecked")
  public AdjacencyList(int vertexCount) {
    this.vertexCount = vertexCount;
    this.matrix = new LinkedList[vertexCount];
    for (int i = 0; i < vertexCount; i++) {
      this.matrix[i] = new LinkedList<Integer>();
    }
  }

  public void addEdge(int i, int j) {
    if (i >= 0 && i < vertexCount && j > 0 && j < vertexCount) {
      this.matrix[i].add(j);
      this.matrix[j].add(i);
    } else throw new IndexOutOfBoundsException();
  }

  public void removeEdge(int i, int j) {
    if (i >= 0 && i < vertexCount && j > 0 && j < vertexCount) {
      this.matrix[i].remove(i);
      this.matrix[j].remove(j);
    } else throw new IndexOutOfBoundsException();
  }

  public List<Integer> getAdjecentEdges(int vertex) {
    if (vertex >= 0 && vertex < vertexCount) {
      return this.matrix[vertex];
    } else throw new IndexOutOfBoundsException();
  }
}
