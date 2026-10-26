/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Neighbours are tried in the order up, right, down, left. DFS is iterative (own stack), so a 1000 x 1000 maze is safe.
 */
public class Escape {

    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells(), rows = w.rows(), cols = w.cols();
        int[][] g = Graphs.open(w, broken);

        int deadEnds = 0;
        for (int i = 0; i < n; i++) if (g[i].length == 1) deadEnds++;

        int exit = n - 1;
        int[] parent = new int[n];
        boolean[] seen = new boolean[n];
        byte[] nextDir = new byte[n];          // which of the 4 directions to try next from each cell
        int[] stack = new int[n];
        int top = 0, visited = 0;

        stack[top++] = 0;
        seen[0] = true;
        parent[0] = -1;
        visited = 1;

        while (top > 0 && !seen[exit]) {
            int cell = stack[top - 1];
            int r = cell / cols, c = cell % cols;
            boolean moved = false;
            while (nextDir[cell] < 4) {
                int d = nextDir[cell]++;
                int nb;
                switch (d) {
                    case 0 -> nb = r > 0 ? cell - cols : -1;
                    case 1 -> nb = c < cols - 1 ? cell + 1 : -1;
                    case 2 -> nb = r < rows - 1 ? cell + cols : -1;
                    default -> nb = c > 0 ? cell - 1 : -1;
                }
                if (nb < 0 || seen[nb] || !contains(g[cell], nb)) continue;
                seen[nb] = true;               // marked here: this is the moment "visited" counts
                visited++;
                parent[nb] = cell;
                stack[top++] = nb;
                moved = true;
                break;
            }
            if (!moved) top--;                 // all neighbours tried: backtrack
        }

        int[] path;
        if (!seen[exit]) {
            path = new int[0];
        } else {
            int len = 1;
            for (int v = exit; v != 0; v = parent[v]) len++;
            path = new int[len];
            int i = len - 1;
            for (int v = exit; v != -1; v = parent[v]) path[i--] = v;
        }
        return new Result(path, visited, deadEnds);
    }

    private static boolean contains(int[] list, int x) {
        for (int v : list) if (v == x) return true;
        return false;
    }
}