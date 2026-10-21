package shortestpath;

import java.util.*;

public class ShortestPathResult {
    public final long[] distances;
    public final int[] previous;
    public final long operations;
    public static final long INF = Long.MAX_VALUE / 4;

    public ShortestPathResult(long[] distances, int[] previous, long operations) {
        this.distances = distances.clone(); this.previous = previous.clone(); this.operations = operations;
    }

    public List<Integer> pathTo(int target) {
        if (distances[target] >= INF) return List.of();
        LinkedList<Integer> path = new LinkedList<>();
        for (int at = target; at != -1; at = previous[at]) path.addFirst(at);
        return path;
    }
}
