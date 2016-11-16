package structures.graph;

import structures.graph.Vertex;

public class Edge<V extends Vertex<V>> {
  V vertex;
  float weight;

  public Edge(V v, float w) {
    this.vertex = v;
    this.weight = w;
  }

  public V vertex() {
    return vertex;
  }

  public float weight() {
    return weight;
  }

  @Override
  public boolean equals(Object other) {
      if (other == null) return false;
      if (other == this) return true;
      if (!(other instanceof Edge)) return false;
      return this.vertex == ((Edge)other).vertex;
    }

    @Override
    public int hashCode() {
      return this.vertex.hashCode();
    }
}
