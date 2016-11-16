package structures.graph;

import structures.list.List;

public abstract class Vertex<V extends Vertex<V>> {
  protected int value;
  protected Float distance = null;
  protected V previous = null;

  public final V previous() {
    return this.previous;
  }

  public final void setPrevious(V prev) {
    this.previous = prev;
  }

  public final Float distance() {
    return this.distance;
  }

  public final void setDistance(Float d) {
    this.distance = d;
  }

  public final int getValue() {
    return this.value;
  }

  public abstract List<? extends V> adjacentVertices();

  public float weight() {
    return 1;
  }
}
