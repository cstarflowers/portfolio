package implement;

import refactor.DisjointSet;
import refactor.Edge;
import refactor.StaticGraph;
import refactor.Vertex;
import refactor.VertexDistance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * Your implementation of various different graph algorithms.
 */
public class GraphAlgorithms {
    /**
     * Performs a breadth-first search (BFS) on the input graph, starting at
     * the parameterized starting vertex.
     * <p>
     * When exploring a vertex, explore in the order of neighbors returned by
     * the adjacency list. Failure to do so may cause you to lose points.
     *
     * @param <T>   the generic typing of the data
     * @param start the vertex to begin the BFS on
     * @param graph the graph to search through
     * @return list of vertices in visited order
     * @throws IllegalArgumentException if any input is null, or if start
     *                                  doesn't exist in the graph
     */
    public static <T> List<Vertex<T>> bfs(Vertex<T> start, StaticGraph<T> graph) {
        if (start == null || graph == null) {
            throw new IllegalArgumentException("The starting vertex/graph can not be null.");
        } else if (!graph.containsVertex(start)) {
            throw new IllegalArgumentException("The starting vertex must be contained in the graph!");
        }
        Set<Vertex<T>> visited = new HashSet<>();
        Queue<Vertex<T>> queue = new LinkedList<>();
        List<Vertex<T>> out = new LinkedList<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            Vertex<T> v = queue.remove();
            out.add(v);
            for (VertexDistance<T> w : graph.getNeighbors(v)) {
                if (!visited.contains(w.vertex())) {
                    queue.add(w.vertex());
                    visited.add(w.vertex());
                }
            }
        }
        return out;
    }

    /**
     * Performs a depth-first search (DFS) on the input graph, starting at
     * the parameterized starting vertex.
     * <p>
     * When exploring a vertex, explore in the order of neighbors returned by
     * the adjacency list. Failure to do so may cause you to lose points.
     *
     * @param <T>   the generic typing of the data
     * @param start the vertex to begin the DFS on
     * @param graph the graph to search through
     * @return list of vertices in visited order
     * @throws IllegalArgumentException if any input is null, or if start
     *                                  doesn't exist in the graph
     * @implSpec You MUST implement this method recursively, or else you will
     * lose all points for this method.
     */
    public static <T> List<Vertex<T>> dfs(Vertex<T> start, StaticGraph<T> graph) {
        if (start == null || graph == null) {
            throw new IllegalArgumentException("The starting vertex/graph can not be null.");
        } else if (!graph.containsVertex(start)) {
            throw new IllegalArgumentException("The starting vertex must be contained in the graph!");
        }
        Set<Vertex<T>> visited = new HashSet<>();
        List<Vertex<T>> out = new LinkedList<>();
        visited.add(start);
        out.add(start);
        for (VertexDistance<T> v : graph.getNeighbors(start)) {
            if (!visited.contains(v.vertex())) {
                dfs(v.vertex(), graph, visited, out);
            }
        }
        return out;
    }

    /**
     * Recursive helper method for DFS.
     * 
     * @param <T>   the generic typing of the data
     * @param curr  the current vertex being explored
     * @param g     the graph being searched through
     * @param vSet  the set of vertices that have already been visited
     * @param list  the list of vertices in visited order
     */
    private static <T> void dfs(Vertex<T> curr, StaticGraph<T> g, Set<Vertex<T>> vSet, List<Vertex<T>> list) {
        vSet.add(curr);
        list.add(curr);
        for (VertexDistance<T> w : g.getNeighbors(curr)) {
            if (!vSet.contains(w.vertex())) {
                dfs(w.vertex(), g, vSet, list);
            }
        }
    }

    /**
     * Finds the single-source shortest distance between the start vertex and
     * all vertices given a weighted graph (you may assume non-negative edge
     * weights).
     * <p>
     * Return a map of the shortest distances such that the key of each entry
     * is a node in the graph and the value for the key is the shortest distance
     * to that node from start, or {@link Integer#MAX_VALUE} (representing
     * infinity) if no path exists.
     *
     * @param <T>   the generic typing of the data
     * @param start the vertex to begin the Dijkstra's on (source)
     * @param graph the graph we are applying Dijkstra's to
     * @return a map of the shortest distances from start to every
     * other node in the graph
     * @throws IllegalArgumentException if any input is null, or if start
     *                                  doesn't exist in the graph.
     */
    public static <T> Map<Vertex<T>, Integer> dijkstras(Vertex<T> start,
                                                        StaticGraph<T> graph) {
        if (start == null || graph == null) {
            throw new IllegalArgumentException("The starting vertex/graph can not be null.");
        } else if (!graph.containsVertex(start)) {
            throw new IllegalArgumentException("The starting vertex must be contained in the graph!");
        }
        Set<Vertex<T>> visited = new HashSet<>();
        Map<Vertex<T>, Integer> distances = new HashMap<>();
        PriorityQueue<VertexDistance<T>> queue = new PriorityQueue<>();
        for (VertexDistance<T> v : graph.getNeighbors(start)) {
            distances.put(v.vertex(), Integer.MAX_VALUE);
        }
        queue.add(new VertexDistance<>(start, 0));
        distances.put(start, 0);
        while (!queue.isEmpty() && visited.size() < graph.getVertexCount()) {
            VertexDistance<T> u = queue.remove();
            if (!visited.contains(u.vertex())) {
                visited.add(u.vertex());
                distances.put(u.vertex(), u.distance());
                for (VertexDistance<T> v : graph.getNeighbors(u.vertex())) {
                    if (!visited.contains(v.vertex())) {
                        queue.add(new VertexDistance<>(v.vertex(), u.distance() + v.distance()));
                    }
                }
            }
        }
        return distances;
    }

    /**
     * Runs Prim's algorithm on the given graph and returns the Minimum
     * Spanning Tree (MST) in the form of a set of Edges. If the graph is
     * disconnected and therefore no valid MST exists, return null.
     * <p>
     * You may assume that the passed in graph is undirected.
     * <p>
     * @param <T> the generic typing of the data
     * @param start the vertex to begin Prims on
     * @param graph the graph we are applying Prims to
     * @return the MST of the graph or null if there is no valid MST
     * @throws IllegalArgumentException if any input is null, or if start
     *                                  doesn't exist in the graph.
     */
    public static <T> Set<Edge<T>> prims(Vertex<T> start, StaticGraph<T> graph) {
        if (start == null || graph == null) {
            throw new IllegalArgumentException("The starting vertex/graph can not be null.");
        } else if (!graph.containsVertex(start)) {
            throw new IllegalArgumentException("The starting vertex must be contained in the graph!");
        }
        Set<Vertex<T>> visited = new HashSet<>();
        Set<Edge<T>> mst = new HashSet<>();
        PriorityQueue<Edge<T>> queue = new PriorityQueue<>();
        for (VertexDistance<T> vd : graph.getNeighbors(start)) {
            queue.add(new Edge<>(vd.vertex(), start, vd.distance()));
        }
        while (!queue.isEmpty() && visited.size() < graph.getVertexCount()) {
            Edge<T> e = queue.remove();
            if (!visited.contains(e.u())) {
                mst.add(e);
                visited.add(e.u());
                for (VertexDistance<T> vd : graph.getNeighbors(e.u())) {
                    if (!visited.contains(vd.vertex())) {
                        queue.add(new Edge<>(vd.vertex(), e.u(), vd.distance()));
                    }
                }
            }
        }
        if (graph.getVertexCount() != mst.size() + 1) {
            return null;
        }
        return mst;
    }

    /**
     * Runs Kruskal's algorithm on the given graph and returns the Minimal
     * Spanning Tree (MST) in the form of a set of Edges. If the graph is
     * disconnected and therefore no valid MST exists, return null.
     * <p>
     * You may assume that the passed in graph is undirected.
     * <p>
     * @param <T>   the generic typing of the data
     * @param graph the graph we are applying Kruskals to
     * @return the MST of the graph or null if there is no valid MST
     * @throws IllegalArgumentException if any input is null
     */
    public static <T> Set<Edge<T>> kruskals(StaticGraph<T> graph) {
        if (graph == null) {
            throw new IllegalArgumentException("The starting graph can not be null.");
        }
        DisjointSet<T> disjointSet = new DisjointSet<>();
        Set<Edge<T>> mst = new HashSet<>();
        PriorityQueue<Edge<T>> edges = new PriorityQueue<>(graph.getEdges());
        while (!edges.isEmpty() && mst.size() < graph.getVertexCount() - 1) {
            Edge<T> e = edges.remove();
            if (!disjointSet.find(e.u().data()).equals(disjointSet.find(e.v().data()))) {
                mst.add(e);
                disjointSet.union(e.u().data(), e.v().data());
            }
        }
        if (graph.getVertexCount() != mst.size() + 1) {
            return null;
        }
        return mst;
    }
}
