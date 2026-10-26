[Uploading README.md…]()
# Maze Escape (Assignment 3)

**Student ID:** 254123
**Course:** Design and Analysis of Algorithms, Java 17+

## 1. Program output

Run: `javac -d out src/*.java` then `java -cp out Main` (data made with `java tools/MakeVariant.java 254123 data`).

```
Broken: 119
Kept: 99
Last broken wall: 183
MST cost: 37788
Heaviest broken wall: 746
Path length: 30
DFS visited: 73
Dead ends: 32
Zones: 9
Largest zone: 21
Is DAG: 0
Flow stages: 6
Fastest route cost: 9
```

The picture of the maze is in `output/maze.png`.

## 2. Answers to the questions

**Mission 1. Why is Broken always R*C - 1 and Kept always (R-1)*(C-1)?**
At the start every cell is its own set, so there are R*C sets. A wall is broken only when `union` returns true, and every such union decreases the number of sets by exactly one. The cells of a grid are all connected through the walls, so in the end there is one set. Going from R*C sets to 1 set takes exactly R*C - 1 successful unions, so Broken = R*C - 1. The total number of walls is R*(C-1) + C*(R-1) = 2RC - R - C. The walls that stay are the rest: 2RC - R - C - (RC - 1) = RC - R - C + 1 = (R-1)*(C-1).

**Mission 3. Why is there exactly one path from the start to the exit?**
The broken walls connect all cells (one set) and there are exactly R*C - 1 of them. A wall is never broken if its two cells are already connected, so no broken wall closes a cycle. A connected graph without cycles is a tree, and in a tree there is exactly one simple path between any two vertices. DFS finds this path.

**Mission 4. Why does the second pass go on the reversed graph?**
The first pass gives the cells in order of finishing. The cell that finishes last lies in a zone that has no slides coming into it from other zones (a source zone of the zone graph). If we start the second DFS from it on the original slides, it would leak into other zones that are reachable from it. On the reversed slides it can reach only the cells that can reach it by the original slides, and together with the fact that it is reachable from them, this is exactly its own zone: a leak into another zone is impossible, because it would need a slide coming into our zone from outside. After the zone is removed, the next not-yet-numbered cell with the latest finishing time is again a source of what is left. So every DFS of the second pass collects exactly one zone, and the zones are found in a topological order.

**Why are there no cycles exactly when Zones equals the number of cells?**
A zone is a set of cells that can reach each other. If there is a cycle of slides, all cells on it can reach each other, so they are in one zone with at least 2 cells, and then Zones < number of cells. If there is no cycle, no two different cells can reach each other (that would be a cycle), so every cell is a zone of its own and Zones = number of cells.

**Mission 5. Why is the order of the zone numbers enough (no priority queue)?**
Zones are numbered in the order of the second pass, which is a topological order: every slide between two zones goes from a smaller number to a larger number. When we reach zone X in the loop, all zones that have a slide into X have smaller numbers and have already been processed, so the cheapest cost to X and the longest chain to X are final. We never need to take "the closest unfinished zone", which is the job of the priority queue in Dijkstra. The pass visits every cell and every slide once, so its cost is O(n + S) (plus the counting sort of cells by zone, also O(n)).

## 3. AI use

<<< WRITE HERE HONESTLY WHAT YOU DID. For example: which tools you used (Claude), for which missions, what the tool wrote, what you checked and changed yourself, and what you understood. Do not leave this section empty or generic. >>>

## 4. Timing (Mission 7)

Run: `java -cp out Bench` (average of 5 runs, milliseconds).

| Size | Kruskal, ms | Prim, ms |
|------|-------------|----------|
| 100 x 100 | 12 | 12 |
| 300 x 300 | 27 | 88 |
| 1000 x 1000 | 756 | 2764 |

From 100x100 to 300x300, the number of cells grows 9 times, while Kruskal's average time grows about 2.25 times (12 to 27 ms) and Prim's grows about 7.3 times (12 to 88 ms). From 300x300 to 1000x1000, the number of cells grows about 11.1 times, while Kruskal's time grows 28 times (27 to 756 ms) and Prim's grows about 31.4 times (88 to 2764 ms). Kruskal is faster on the larger mazes because it sorts the edges once and then uses efficient DSU operations, while Prim repeatedly manages edges in a priority queue. Both algorithms are expected to have roughly O(E log E) time complexity, but actual timings depend on implementation and runtime conditions.

## 5. Complexity

V = number of cells (R*C), E = number of walls (about 2V), S = number of slides.

| Algorithm | Time | Space |
|-----------|------|-------|
| DSU `find` / `union` (size + path compression) | O(alpha(V)), almost O(1) | O(V) |
| Mission 1: break walls in file order | O(E * alpha(V)) | O(V) |
| Kruskal (sort + DSU) | O(E log E) | O(E + V) |
| Prim (priority queue) | O(E log E) = O(E log V) | O(E + V) |
| DFS escape (iterative) | O(V) (the maze is a tree, so its edges are V - 1) | O(V) |
| Kosaraju (two passes) | O(V + S) | O(V + S) |
| Fastest route over the zone order | O(V + S) | O(V + S) |
