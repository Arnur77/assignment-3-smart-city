package mst;

import graph.Edge;
import java.util.*;

public class MSTResult {
    public final boolean connected;
    public final long totalWeight;
    public final List<Edge> edges;
    public final long operations;

    public MSTResult(boolean connected, long totalWeight, List<Edge> edges, long operations) {
        this.connected = connected;
        this.totalWeight = totalWeight;
        this.edges = List.copyOf(edges);
        this.operations = operations;
    }
}
