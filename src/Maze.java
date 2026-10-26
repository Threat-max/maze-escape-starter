import java.util.*;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] broken = new int[Math.max(0, w.cells() - 1)];
        int m = 0;
        for (int k = 0; k < w.count() && m < broken.length; k++) {
            if (dsu.union(w.a()[k], w.b()[k])) broken[m++] = k;
        }
        return Arrays.copyOf(broken, m);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        int count = w.count();
        // sort (cost, index) pairs packed in one long: no boxing, O(E log E)
        long[] keys = new long[count];
        for (int k = 0; k < count; k++) keys[k] = ((long) w.cost()[k] << 32) | k;
        Arrays.sort(keys);
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] broken = new int[Math.max(0, w.cells() - 1)];
        int m = 0;
        for (int i = 0; i < count && m < broken.length; i++) {
            int k = (int) (keys[i] & 0xFFFFFFFFL);
            if (dsu.union(w.a()[k], w.b()[k])) broken[m++] = k;
        }
        return Arrays.copyOf(broken, m);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells(), count = w.count();
        // adjacency in arrays: start[v] .. start[v+1] holds the wall indexes touching cell v
        int[] start = new int[n + 1];
        for (int k = 0; k < count; k++) { start[w.a()[k] + 1]++; start[w.b()[k] + 1]++; }
        for (int i = 0; i < n; i++) start[i + 1] += start[i];
        int[] adj = new int[2 * count];
        int[] fill = Arrays.copyOf(start, n);
        for (int k = 0; k < count; k++) { adj[fill[w.a()[k]]++] = k; adj[fill[w.b()[k]]++] = k; }

        boolean[] inTree = new boolean[n];
        PriorityQueue<Long> pq = new PriorityQueue<>();   // element = cost << 32 | cell the wall leads to
        long total = 0;
        int taken = 0;
        inTree[0] = true;
        taken = 1;
        for (int i = start[0]; i < start[1]; i++) {
            int k = adj[i];
            int to = w.a()[k] == 0 ? w.b()[k] : w.a()[k];
            pq.add(((long) w.cost()[k] << 32) | to);
        }
        while (!pq.isEmpty() && taken < n) {
            long top = pq.poll();
            int v = (int) (top & 0xFFFFFFFFL);
            if (inTree[v]) continue;
            inTree[v] = true;
            taken++;
            total += top >>> 32;
            for (int i = start[v]; i < start[v + 1]; i++) {
                int k = adj[i];
                int to = w.a()[k] == v ? w.b()[k] : w.a()[k];
                if (!inTree[to]) pq.add(((long) w.cost()[k] << 32) | to);
            }
        }
        return total;
    }
}