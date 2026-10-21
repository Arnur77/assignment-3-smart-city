package mst;

import dsu.DisjointSetUnion;
import graph.Edge;
import graph.Graph;
import java.util.*;

public class Kruskal {
    public static MSTResult findMST(Graph graph) {
        List<Edge> sorted = new ArrayList<>(graph.edges());
        sorted.sort(Comparator.comparingLong(e -> e.weight));
        DisjointSetUnion dsu = new DisjointSetUnion(graph.vertexCount());
        List<Edge> result = new ArrayList<>();
        long comparisons = 0;
        for (Edge e : sorted) {
            comparisons++;
            if (dsu.union(e.from, e.to)) {
                result.add(e);
                if (result.size() == Math.max(0, graph.vertexCount() - 1)) break;
            }
        }
        boolean connected = graph.vertexCount() <= 1 || result.size() == graph.vertexCount() - 1;
        long ops = comparisons + dsu.getFindOperations() + dsu.getUnionOperations();
        long total = result.stream().mapToLong(e -> e.weight).sum();
        return new MSTResult(connected, total, result, ops);
    }
}
