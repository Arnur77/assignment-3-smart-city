package graph;

import java.util.*;

public class DirectedGraph {
    private final int vertices;
    private final List<Edge> edges = new ArrayList<>();
    private final List<List<Edge>> adjacency;

    public DirectedGraph(int vertices) {
        if (vertices < 0) throw new IllegalArgumentException("vertices must be non-negative");
        this.vertices = vertices;
        adjacency = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) adjacency.add(new ArrayList<>());
    }

    public int vertexCount() { return vertices; }
    public List<Edge> edges() { return Collections.unmodifiableList(edges); }
    public List<Edge> neighbors(int v) { return Collections.unmodifiableList(adjacency.get(v)); }

    public void addEdge(int u, int v, long weight) {
        checkVertex(u); checkVertex(v);
        Edge e = new Edge(u, v, weight);
        edges.add(e);
        adjacency.get(u).add(e);
    }

    public DirectedGraph reversed() {
        DirectedGraph r = new DirectedGraph(vertices);
        for (Edge e : edges) r.addEdge(e.to, e.from, e.weight);
        return r;
    }

    private void checkVertex(int v) {
        if (v < 0 || v >= vertices) throw new IndexOutOfBoundsException("Invalid vertex: " + v);
    }
}
