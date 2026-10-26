import java.util.*;

/**
 * Missions 4 and 5. The slides: a directed graph.
 */
public class Zones {

    /**
     * Mission 4. Kosaraju with iterative DFS.
     * Pass 1: DFS on the slides, record the finishing order.
     * Pass 2: go through cells from the last finished; DFS on the reversed slides gives one zone each time.
     * Zones are numbered in the order pass 2 finds them (a topological order of the zone graph).
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s);
        int[][] in = Graphs.slidesInto(s);
        int[] to = s.to(), from = s.from();

        // pass 1
        boolean[] seen = new boolean[n];
        int[] order = new int[n];
        int finished = 0;
        int[] stack = new int[n];
        int[] idx = new int[n];
        for (int root = 0; root < n; root++) {
            if (seen[root]) continue;
            int top = 0;
            stack[top++] = root;
            seen[root] = true;
            while (top > 0) {
                int u = stack[top - 1];
                if (idx[u] < out[u].length) {
                    int v = to[out[u][idx[u]++]];
                    if (!seen[v]) { seen[v] = true; stack[top++] = v; }
                } else {
                    order[finished++] = u;
                    top--;
                }
            }
        }

        // pass 2 on the reversed graph
        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int zones = 0;
        for (int i = n - 1; i >= 0; i--) {
            int root = order[i];
            if (zoneOf[root] != -1) continue;
            int top = 0;
            stack[top++] = root;
            zoneOf[root] = zones;
            while (top > 0) {
                int u = stack[--top];
                for (int e : in[u]) {
                    int v = from[e];
                    if (zoneOf[v] == -1) { zoneOf[v] = zones; stack[top++] = v; }
                }
            }
            zones++;
        }
        return zoneOf;
    }

    /**
     * Mission 5. One pass over the zones in the order of their numbers (a topological order), O(n + S).
     * [0] flow stages = zones on the longest chain; [1] cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s);
        int[] to = s.to(), time = s.time();

        // cells grouped by zone (counting sort)
        int[] begin = new int[zoneCount + 1];
        for (int c = 0; c < n; c++) begin[zoneOf[c] + 1]++;
        for (int z = 0; z < zoneCount; z++) begin[z + 1] += begin[z];
        int[] cells = new int[n];
        int[] fill = Arrays.copyOf(begin, zoneCount);
        for (int c = 0; c < n; c++) cells[fill[zoneOf[c]]++] = c;

        final long INF = Long.MAX_VALUE / 4;
        long[] dist = new long[zoneCount];
        long[] chain = new long[zoneCount];
        Arrays.fill(dist, INF);
        Arrays.fill(chain, 1);
        dist[zoneOf[0]] = 0;
        long stages = 1;

        for (int z = 0; z < zoneCount; z++) {
            stages = Math.max(stages, chain[z]);
            for (int i = begin[z]; i < begin[z + 1]; i++) {
                int u = cells[i];
                for (int e : out[u]) {
                    int zt = zoneOf[to[e]];
                    if (zt == z) continue;            // inside a zone: free, not an edge of the zone graph
                    if (chain[z] + 1 > chain[zt]) chain[zt] = chain[z] + 1;
                    if (dist[z] < INF && dist[z] + time[e] < dist[zt]) dist[zt] = dist[z] + time[e];
                }
            }
        }
        long best = dist[zoneOf[n - 1]];
        return new long[]{stages, best >= INF ? -1 : best};
    }
}