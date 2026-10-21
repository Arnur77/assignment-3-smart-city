package shortestpath;

import graph.DirectedGraph;
import graph.Edge;
import java.util.*;

public class Dijkstra {
    private record State(int vertex, long distance) {}

    public static ShortestPathResult shortestPaths(DirectedGraph graph, int source) {
        int n = graph.vertexCount();
        long[] dist = new long[n];
        int[] prev = new int[n];
        Arrays.fill(dist, ShortestPathResult.INF);
        Arrays.fill(prev, -1);
        dist[source] = 0;
        PriorityQueue<State> pq = new PriorityQueue<>(Comparator.comparingLong(State::distance));
        pq.add(new State(source, 0));
        long operations = 0;

        while (!pq.isEmpty()) {
            State cur = pq.poll();
            operations++;
            if (cur.distance != dist[cur.vertex]) continue;
            for (Edge e : graph.neighbors(cur.vertex)) {
                operations++;
                if (e.weight < 0) throw new IllegalArgumentException("Dijkstra requires non-negative weights");
                long candidate = cur.distance + e.weight;
                if (candidate < dist[e.to]) {
                    dist[e.to] = candidate;
                    prev[e.to] = cur.vertex;
                    pq.add(new State(e.to, candidate));
                    operations++;
                }
            }
        }
        return new ShortestPathResult(dist, prev, operations);
    }
}
