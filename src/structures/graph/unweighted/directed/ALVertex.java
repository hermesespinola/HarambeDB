package structures.graph.unweighted.directed;

import structures.list.LinkedList;
import structures.graph.Vertex;

public class ALVertex extends Vertex<ALVertex> {
  LinkedList<ALVertex> adjacentVertices;
  private static final long serialVersionUID = 31l;

  public ALVertex(int value) {
    this.value = value;
    this.adjacentVertices = new LinkedList<>();
  }

  public ALVertex(int value, LinkedList<ALVertex> adjacentVertices) {
    this.value = value;
    this.adjacentVertices = adjacentVertices;
  }

  public void connectVertex(ALVertex other) {
    this.adjacentVertices.add(0, other);
  }

  public void removeAdjacentVertex(ALVertex other) {
    this.adjacentVertices.remove(adjacentVertices.indexOf(other));
  }

  public LinkedList<ALVertex> adjacentVertices() {
    return this.adjacentVertices;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder().append(this.value).append(": [ ");
    for (ALVertex v : adjacentVertices) {
      sb.append(v.value).append(' ');
    }
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
