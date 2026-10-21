package mst;

import graph.Edge;
import graph.Graph;
import java.util.*;

public class Prim {
    public static MSTResult findMST(Graph graph) {
        int n = graph.vertexCount();
        if (n == 0) return new MSTResult(true, 0, List.of(), 0);
        boolean[] used = new boolean[n];
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingLong(e -> e.weight));
        List<Edge> result = new ArrayList<>();
        long operations = 0;
        long total = 0;
        used[0] = true;
        pq.addAll(graph.neighbors(0));
        operations += graph.neighbors(0).size();

        while (!pq.isEmpty() && result.size() < n - 1) {
            Edge e = pq.poll();
            operations++;
            if (used[e.to]) continue;
            used[e.to] = true;
            result.add(new Edge(e.from, e.to, e.weight));
            total += e.weight;
            for (Edge next : graph.neighbors(e.to)) {
                operations++;
                if (!used[next.to]) pq.add(next);
            }
        }
        boolean connected = result.size() == n - 1;
        return new MSTResult(connected, total, result, operations);
    }
}
