/**
 * Checks of Mission 0. Run:  java -cp out DisjointSetTest
 * Add your own checks below: a large chain, a union of a set with itself, count after many unions.
 */
public class DisjointSetTest {

    static int failed = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok    " : "FAIL  ") + what);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        DisjointSet d = new DisjointSet(6);
        check(d.count() == 6, "6 sets at the start");
        check(!d.connected(0, 1), "0 and 1 are apart at the start");
        check(d.union(0, 1), "union(0, 1) joins two sets");
        check(d.connected(0, 1), "0 and 1 are together");
        check(!d.union(1, 0), "union(1, 0) again returns false");
        check(d.count() == 5, "5 sets after one union");
        d.union(2, 3); d.union(1, 3);
        check(d.connected(0, 2), "0 and 2 are together through 1 and 3");
        check(d.count() == 3, "3 sets: {0,1,2,3} {4} {5}");
        check(d.find(4) == 4 && d.find(5) == 5, "a single cell is its own root");

        // a union of a set with itself
        check(!d.union(2, 2), "union(2, 2) returns false");
        check(d.count() == 3, "count is unchanged after a useless union");

        // a large chain: no stack overflow, one set at the end
        int n = 1_000_000;
        DisjointSet big = new DisjointSet(n);
        boolean allTrue = true;
        for (int i = 0; i + 1 < n; i++) allTrue &= big.union(i, i + 1);
        check(allTrue, "every union of a chain of 1000000 cells returns true");
        check(big.count() == 1, "1 set after the chain");
        check(big.connected(0, n - 1), "the ends of the chain are together");
        check(!big.union(0, n - 1), "union of the ends returns false");

        // count after many unions
        DisjointSet e = new DisjointSet(10);
        for (int i = 0; i < 10; i += 2) e.union(i, (i + 1) % 10);
        check(e.count() == 5, "5 sets after 5 disjoint pair unions");
        e.union(0, 2); e.union(4, 6);
        check(e.count() == 3, "3 sets after joining pairs");
        check(e.connected(1, 3) && !e.connected(1, 5), "connected follows the unions");

        // an empty DSU and a single cell
        check(new DisjointSet(1).count() == 1, "one cell is one set");

        System.out.println(failed == 0 ? "All checks passed." : failed + " checks failed.");
    }
}