package connectivity;

import graph.DirectedGraph;
import graph.Edge;
import java.util.*;

public class KosarajuSCC {
    public static Result findSCCs(DirectedGraph graph) {
        int n = graph.vertexCount();
        boolean[] visited = new boolean[n];
        List<Integer> order = new ArrayList<>(n);
        long[] ops = {0};
        for (int i = 0; i < n; i++) if (!visited[i]) dfs1(graph, i, visited, order, ops);

        DirectedGraph rev = graph.reversed();
        Arrays.fill(visited, false);
        List<List<Integer>> components = new ArrayList<>();
        for (int i = order.size() - 1; i >= 0; i--) {
            int v = order.get(i);
            if (!visited[v]) {
                List<Integer> component = new ArrayList<>();
                dfs2(rev, v, visited, component, ops);
                components.add(component);
            }
        }
        return new Result(components, ops[0]);
    }

    private static void dfs1(DirectedGraph g, int u, boolean[] visited, List<Integer> order, long[] ops) {
        visited[u] = true;
        for (Edge e : g.neighbors(u)) {
            ops[0]++;
            if (!visited[e.to]) dfs1(g, e.to, visited, order, ops);
        }
        order.add(u);
    }

    private static void dfs2(DirectedGraph g, int u, boolean[] visited, List<Integer> component, long[] ops) {
        visited[u] = true; component.add(u);
        for (Edge e : g.neighbors(u)) {
            ops[0]++;
            if (!visited[e.to]) dfs2(g, e.to, visited, component, ops);
        }
    }

    public static class Result {
        public final List<List<Integer>> components;
        public final long operations;
        public Result(List<List<Integer>> components, long operations) {
            this.components = List.copyOf(components); this.operations = operations;
        }
    }
}
