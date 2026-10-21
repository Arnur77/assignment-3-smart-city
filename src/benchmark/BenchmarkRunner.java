package benchmark;

import connectivity.KosarajuSCC;
import connectivity.TopologicalSort;
import graph.DirectedGraph;
import graph.Graph;
import mst.Kruskal;
import mst.Prim;
import shortestpath.Dijkstra;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class BenchmarkRunner {
    private static final long SEED = 20261021L;
    private static final int REPEATS = 5;
    private static final int[][] SIZES = {{10,30},{100,500},{1000,5000}};

    public static void main(String[] args) throws Exception {
        Path out = Paths.get("results", "tables");
        Files.createDirectories(out);
        benchmarkMST(out.resolve("mst_results.csv"));
        benchmarkDirected(out.resolve("directed_results.csv"));
        benchmarkDijkstra(out.resolve("dijkstra_results.csv"));
        System.out.println("Benchmark complete. CSV files written to " + out.toAbsolutePath());
    }

    private static void benchmarkMST(Path file) throws IOException {
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(file))) {
            w.println("algorithm,vertices,edges,average_time_ns,operations");
            for (int[] s : SIZES) {
                Graph g = generateConnectedGraph(s[0], s[1], SEED + s[0]);
                // Warm-up without recording time.
                Kruskal.findMST(g); Prim.findMST(g);
                for (String alg : List.of("Kruskal", "Prim")) {
                    long time = 0, ops = 0;
                    for (int i = 0; i < REPEATS; i++) {
                        long start = System.nanoTime();
                        var r = alg.equals("Kruskal") ? Kruskal.findMST(g) : Prim.findMST(g);
                        time += System.nanoTime() - start;
                        ops += r.operations;
                    }
                    w.printf(Locale.US, "%s,%d,%d,%d,%d%n", alg, s[0], s[1], time / REPEATS, ops / REPEATS);
                }
            }
        }
    }

    private static void benchmarkDirected(Path file) throws IOException {
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(file))) {
            w.println("algorithm,graph_type,vertices,edges,average_time_ns,operations");
            for (int[] s : SIZES) {
                DirectedGraph dag = generateDag(s[0], s[1], SEED + 100 + s[0]);
                DirectedGraph cyclic = generateCyclicFromDag(dag);
                for (String alg : List.of("SCC", "Topological")) {
                    DirectedGraph g = alg.equals("SCC") ? dag : dag;
                    long time = 0, ops = 0;
                    for (int i = 0; i < REPEATS; i++) {
                        long start = System.nanoTime();
                        if (alg.equals("SCC")) {
                            var r = KosarajuSCC.findSCCs(g); time += System.nanoTime() - start; ops += r.operations;
                        } else {
                            var r = TopologicalSort.sort(g); time += System.nanoTime() - start; ops += r.operations;
                        }
                    }
                    w.printf(Locale.US, "%s,DAG,%d,%d,%d,%d%n", alg, s[0], s[1], time/REPEATS, ops/REPEATS);
                    if (alg.equals("Topological")) {
                        time = 0; ops = 0;
                        for (int i = 0; i < REPEATS; i++) {
                            long start = System.nanoTime();
                            var r = TopologicalSort.sort(cyclic); time += System.nanoTime() - start; ops += r.operations;
                        }
                        w.printf(Locale.US, "%s,Cyclic,%d,%d,%d,%d%n", alg, s[0], cyclic.edges().size(), time/REPEATS, ops/REPEATS);
                    }
                }
            }
        }
    }

    private static void benchmarkDijkstra(Path file) throws IOException {
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(file))) {
            w.println("algorithm,vertices,edges,average_time_ns,operations");
            for (int[] s : SIZES) {
                DirectedGraph g = generateDag(s[0], s[1], SEED + 200 + s[0]);
                Dijkstra.shortestPaths(g, 0);
                long time = 0, ops = 0;
                for (int i = 0; i < REPEATS; i++) {
                    long start = System.nanoTime();
                    var r = Dijkstra.shortestPaths(g, 0);
                    time += System.nanoTime() - start; ops += r.operations;
                }
                w.printf(Locale.US, "Dijkstra,%d,%d,%d,%d%n", s[0], s[1], time/REPEATS, ops/REPEATS);
            }
        }
    }

    private static Graph generateConnectedGraph(int n, int m, long seed) {
        Random r = new Random(seed);
        Graph g = new Graph(n);
        Set<Long> used = new HashSet<>();
        for (int v = 1; v < n; v++) {
            int u = r.nextInt(v);
            addUniqueUndirected(g, used, u, v, 1 + r.nextInt(100));
        }
        while (g.edges().size() < m) {
            int u = r.nextInt(n), v = r.nextInt(n - 1); if (v >= u) v++;
            addUniqueUndirected(g, used, u, v, 1 + r.nextInt(100));
        }
        return g;
    }

    private static void addUniqueUndirected(Graph g, Set<Long> used, int u, int v, long w) {
        int a = Math.min(u,v), b = Math.max(u,v);
        long key = (((long)a) << 32) ^ (b & 0xffffffffL);
        if (a != b && used.add(key)) g.addEdge(a,b,w);
    }

    private static DirectedGraph generateDag(int n, int m, long seed) {
        Random r = new Random(seed);
        DirectedGraph g = new DirectedGraph(n);
        Set<Long> used = new HashSet<>();
        int possible = n * (n - 1) / 2;
        m = Math.min(m, possible);
        while (g.edges().size() < m) {
            int u = r.nextInt(n), v = r.nextInt(n);
            if (u == v || u > v) continue;
            long key = (((long)u) << 32) ^ (v & 0xffffffffL);
            if (used.add(key)) g.addEdge(u, v, 1 + r.nextInt(100));
        }
        return g;
    }

    private static DirectedGraph generateCyclicFromDag(DirectedGraph dag) {
        DirectedGraph g = new DirectedGraph(dag.vertexCount());
        for (var e : dag.edges()) g.addEdge(e.from, e.to, e.weight);
        if (g.vertexCount() >= 2) g.addEdge(g.vertexCount()-1, 0, 1);
        return g;
    }
}
