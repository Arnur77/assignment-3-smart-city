package connectivity;

import graph.DirectedGraph;
import graph.Edge;
import java.util.*;

public class TopologicalSort {
    public static Result sort(DirectedGraph graph) {
        int n = graph.vertexCount();
        int[] indegree = new int[n];
        long operations = 0;
        for (Edge e : graph.edges()) { indegree[e.to]++; operations++; }
        ArrayDeque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indegree[i] == 0) q.add(i);
        List<Integer> order = new ArrayList<>(n);
        while (!q.isEmpty()) {
            int u = q.remove();
            order.add(u);
            for (Edge e : graph.neighbors(u)) {
                operations++;
                if (--indegree[e.to] == 0) q.add(e.to);
            }
        }
        return new Result(order.size() == n, order, operations);
    }

    public static class Result {
        public final boolean acyclic;
        public final List<Integer> order;
        public final long operations;
        public Result(boolean acyclic, List<Integer> order, long operations) {
            this.acyclic = acyclic; this.order = List.copyOf(order); this.operations = operations;
        }
    }
}
