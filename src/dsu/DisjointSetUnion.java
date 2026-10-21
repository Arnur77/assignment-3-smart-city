package dsu;

public class DisjointSetUnion {
    private final int[] parent;
    private final int[] rank;
    private long findOperations;
    private long unionOperations;

    public DisjointSetUnion(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    public int find(int x) {
        findOperations++;
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }

    public boolean union(int a, int b) {
        unionOperations++;
        int ra = find(a), rb = find(b);
        if (ra == rb) return false;
        if (rank[ra] < rank[rb]) {
            parent[ra] = rb;
        } else if (rank[ra] > rank[rb]) {
            parent[rb] = ra;
        } else {
            parent[rb] = ra;
            rank[ra]++;
        }
        return true;
    }

    public long getFindOperations() { return findOperations; }
    public long getUnionOperations() { return unionOperations; }
}
