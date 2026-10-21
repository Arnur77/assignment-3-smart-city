package tests;

import connectivity.KosarajuSCC;
import connectivity.TopologicalSort;
import graph.DirectedGraph;
import graph.Graph;
import mst.Kruskal;
import mst.Prim;
import shortestpath.Dijkstra;
import shortestpath.ShortestPathResult;
import java.util.*;

public class TestRunner {
    private static int passed = 0;
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        passed++;
    }

    public static void main(String[] args) {
        testMST();
        testConnectivity();
        testDijkstra();
        System.out.println("ALL TESTS PASSED: " + passed);
    }

    private static void testMST() {
        Graph g = new Graph(4);
        g.addEdge(0, 1, 1); g.addEdge(1, 2, 1); g.addEdge(2, 3, 2);
        g.addEdge(0, 3, 2); g.addEdge(0, 2, 1); g.addEdge(1, 3, 2); // duplicate weights / multiple MSTs
        var k = Kruskal.findMST(g); var p = Prim.findMST(g);
        check(k.connected && p.connected, "connected MST expected");
        check(k.totalWeight == p.totalWeight, "Kruskal and Prim weights must match");
        Graph disconnected = new Graph(3); disconnected.addEdge(0, 1, 4);
        check(!Kruskal.findMST(disconnected).connected, "disconnected graph must be detected");
        check(!Prim.findMST(disconnected).connected, "Prim must detect disconnected graph");
        Graph single = new Graph(1);
        check(Kruskal.findMST(single).connected && Prim.findMST(single).connected, "single vertex MST");
        check(Kruskal.findMST(single).totalWeight == 0, "single vertex weight");
    }

    private static void testConnectivity() {
        DirectedGraph dag = new DirectedGraph(5);
        dag.addEdge(0, 1, 1); dag.addEdge(0, 2, 1); dag.addEdge(1, 3, 1); dag.addEdge(2, 3, 1); dag.addEdge(3, 4, 1);
        var topo = TopologicalSort.sort(dag);
        check(topo.acyclic && topo.order.size() == 5, "DAG topological ordering");
        Map<Integer,Integer> pos = new HashMap<>();
        for (int i = 0; i < topo.order.size(); i++) pos.put(topo.order.get(i), i);
        for (var e : dag.edges()) check(pos.get(e.from) < pos.get(e.to), "topological order validity");

        DirectedGraph cyclic = new DirectedGraph(3);
        cyclic.addEdge(0, 1, 1); cyclic.addEdge(1, 2, 1); cyclic.addEdge(2, 0, 1);
        check(!TopologicalSort.sort(cyclic).acyclic, "cycle must be detected");

        DirectedGraph scc = new DirectedGraph(5);
        scc.addEdge(0, 1, 1); scc.addEdge(1, 0, 1); scc.addEdge(1, 2, 1); scc.addEdge(2, 3, 1); scc.addEdge(3, 2, 1); scc.addEdge(3, 4, 1);
        var result = KosarajuSCC.findSCCs(scc);
        Set<Set<Integer>> components = new HashSet<>();
        for (var c : result.components) components.add(new HashSet<>(c));
        check(components.contains(Set.of(0,1)), "first SCC");
        check(components.contains(Set.of(2,3)), "second SCC");
        check(components.contains(Set.of(4)), "third SCC");
        check(result.components.stream().mapToInt(List::size).sum() == 5, "every vertex belongs to one SCC");
    }

    private static void testDijkstra() {
        DirectedGraph g = new DirectedGraph(6);
        g.addEdge(0,1,4); g.addEdge(0,2,1); g.addEdge(2,1,2); g.addEdge(1,3,1); g.addEdge(2,3,5); g.addEdge(3,4,3);
        var r = Dijkstra.shortestPaths(g, 0);
        check(r.distances[0] == 0, "source distance");
        check(r.distances[1] == 3, "distance to 1");
        check(r.distances[3] == 4, "distance to 3");
        check(r.distances[4] == 7, "distance to 4");
        check(r.distances[5] >= ShortestPathResult.INF, "unreachable vertex");
        check(r.pathTo(4).equals(List.of(0,2,1,3,4)), "path reconstruction");

        DirectedGraph negative = new DirectedGraph(2); negative.addEdge(0,1,-1);
        boolean thrown = false;
        try { Dijkstra.shortestPaths(negative, 0); } catch (IllegalArgumentException e) { thrown = true; }
        check(thrown, "negative edge must be rejected");
    }
}
