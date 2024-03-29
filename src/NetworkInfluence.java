// LEAVE THIS FILE IN THE DEFAULT PACKAGE
//  (i.e., DO NOT add 'package cs311.pa1;' or similar)

// DO NOT MODIFY THE EXISTING METHOD SIGNATURES
//  (you may, however, add member fields and additional methods)

// DO NOT INCLUDE LIBRARIES OUTSIDE OF THE JAVA STANDARD LIBRARY
//  (i.e., you may only include libraries of the form java.*)

/**
 * @author Matthew Burket
 * @author Joel May
 */

import java.io.*;
import java.util.*;

public class NetworkInfluence {
    /**
     * The count of vertices that exist in the graph. Since we are provided an edge list to populate nodes, this may be
     * larger than nodes.
     */
    private int vertexCount;
    /** A map of all directed edges in the graph */
    private HashMap<String, List<String>> edges = new HashMap<>();
    /** A set of all the nodes in the graph */
    private HashSet<String> nodes = new HashSet<>();

    /**
     * Loads a graph from a file.
     * @param fileName an absolute path to the file containing the graph data. First line is a count of nodes. Each
     *                 following line is an edge denoted by two nodes separated by one or more whitespace characters.
     */
    public NetworkInfluence(String fileName) {
        // implementation
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            vertexCount = Integer.parseInt(bufferedReader.readLine());
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Validations on line format
                if (line.length() == 0) {
                    continue;
                }
                String[] edge = line.split("\\s+");
                if (edge.length != 2) {
                    throw new RuntimeException("Input file is malformed. Must have 2 nodes separated by spaces." +
                            "Line: " + line);
                }

                // Store edge
                List<String> outNodes;
                if (edges.containsKey(edge[0])) {
                    outNodes = edges.get(edge[0]);
                } else {
                    outNodes = new ArrayList<>();
                }
                outNodes.add(edge[1]);
                edges.put(edge[0], outNodes);

                // Store vertices
                nodes.add(edge[0]);
                nodes.add(edge[1]);
            }
        } catch (IOException e) {
            System.err.println("Problem reading the file to load the graph.");
            e.printStackTrace();
        }
    }

    /**
     * Calculates the out degree of a node.
     * @param v the node to calculated the out degree on
     * @return the out degree of the node
     */
    public int outDegree(String v) {
        if (edges.containsKey(v)) {
            return edges.get(v).size();
        }
        return 0;
    }

    /**
     * Does a BFS search to find the shortest path between the nodes.
     * @param u the starting vertex
     * @param v the destination vertex
     * @return a list of nodes that starts with u, ends with v, and is a shortest path between them. Empty list if there
     * is no path.
     */
    public ArrayList<String> shortestPath(String u, String v) {
        final ArrayDeque<String> toVisit = new ArrayDeque<>();
        final HashMap<String, String> parent = new HashMap<>(); // This idea is from Wikipedia: Breadth-first search

        toVisit.add(u);
        parent.put(u, null);
        do {
            String currentNode = toVisit.remove();
            // Did we find the destination (v) node?
            if (currentNode.equals(v)) {
                // Node for navigating back to node u
                String backNode = currentNode;
                // A list used to reverse the back trace
                LinkedList<String> path = new LinkedList<>();

                // Navigate through the backlinks from v to u
                while (!backNode.equals(u)) {
                    path.addFirst(backNode);
                    backNode = parent.get(backNode);
                }
                path.addFirst(backNode);
                return new ArrayList<>(path);
            }
            // If this node has outgoing edges...
            if (edges.containsKey(currentNode)) {
                // ...add each unvisited child to the queue
                edges.get(currentNode).forEach(dst -> {
                    if (!parent.containsKey(dst)) {
                        toVisit.add(dst);
                        parent.put(dst, currentNode);
                    }
                });
            }
        } while (!toVisit.isEmpty());

        // No path was found.
        return new ArrayList<>();
    }

    /**
     * Calculates the distance between two nodes.
     * @param u the starting vertex
     * @param v the destination vertex
     * @return the minimum number of edges needed to traverse between the two nodes. If there is no path, -1 is
     * returned.
     */
    public int distance(String u, String v) {
        int dist = shortestPath(u, v).size();
        return dist - 1;
    }

    /**
     * Calculates the minimum distance between an element in s and v.
     * @param s a subset of vertices that may be a starting vertex
     * @param v the destination vertex
     * @return the minimum distance between any node in s to node v. -1 if there is no path between any node in s and v.
     */
    public int distance(ArrayList<String> s, String v) {
        int minDistance = Integer.MAX_VALUE;
        for (String startNode : s) {
            int thisDistance = distance(startNode, v);
            if (thisDistance != -1 && thisDistance < minDistance) {
                minDistance = thisDistance;
            }
        }
        if (minDistance == Integer.MAX_VALUE) {
            return -1;
        }
        return minDistance;
    }

    /**
     * The influence of the node calculated by Sum[1/2^i * |{y|dist(x,y)==i}|, {i, 0, n}]
     * @param u the node to calculate the influence of
     * @return the decimal value of the influence of the node on the graph
     */
    public float influence(String u) {
        Map<Integer, Integer> tieredCounts = tieredBfs(u);
        float sum = 0;

        for (Map.Entry<Integer, Integer> depthAndCount : tieredCounts.entrySet()) {
            sum += 1 / Math.pow(2, depthAndCount.getKey()) * depthAndCount.getValue();
        }

        return sum;
    }

    /**
     * The influence of the subset on the set, calculated by Sum[1/2^i * |{y|dist(S,y)==i}|, {i, 0, n}]
     * @param s the subset of nodes to find the influence of
     * @return the decimal value of the influence of the subset of nodes on the graph
     */
    public float influence(ArrayList<String> s) {
        Map<Integer, Integer> tieredCounts = tieredBfs(s);
        float sum = 0;

        for (Map.Entry<Integer, Integer> depthAndCount : tieredCounts.entrySet()) {
            sum += 1 / Math.pow(2, depthAndCount.getKey()) * depthAndCount.getValue();
        }

        return sum;
    }

    /**
     * Performs a BFS on the whole graph reachable by startingNode. Counts the number of nodes in each distance. It only
     * considers the shortest path to a node.
     * @param startingNode the node to find distances from
     * @return a TreeMap of the depth (starting at 0, the startingNode) and the count of nodes in that depth
     */
    private Map<Integer, Integer> tieredBfs(String startingNode) {
        ArrayList<String> node = new ArrayList<>();
        node.add(startingNode);
        return tieredBfs(node);
    }

    /**
     * Performs a BFS on the whole graph reachable by all startingNodes. Counts the number of nodes in each distance of
     * the lowest distance starting node. It only considers the shortest path to a node.
     * @param startingNodes the subset to find distances from
     * @return a TreeMap of the depth (starting at 0, with each of the startingNodes) and the count of nodes in that
     * distance
     */
    private Map<Integer, Integer> tieredBfs(Iterable<String> startingNodes) {
        // <depth, count of nodes in that depth>
        HashMap<Integer, Integer> ret = new HashMap<>();
        // Queue of nodes to visit for BFS with distance from startingNode
        final ArrayDeque<Map.Entry<String, Integer>> toVisit = new ArrayDeque<>();
        // The nodes that have been added to the BFS queue or processed
        final HashSet<String> visited = new HashSet<>();

        // Put the starting set into the queue as distances 0
        for (String startingNode : startingNodes) {
            toVisit.add(new AbstractMap.SimpleEntry<>(startingNode, 0));
            visited.add(startingNode);
        }
        // Do the BFS traversal
        do {
            Map.Entry<String, Integer> currentNode = toVisit.remove();

            // Increment the tier's count in the output object
            int newCount = ret.getOrDefault(currentNode.getValue(), 0) + 1;
            ret.put(currentNode.getValue(), newCount);

            // If this node has outgoing edges...
            if (edges.containsKey(currentNode.getKey())) {
                // ...add each unvisited child to the queue
                edges.get(currentNode.getKey()).forEach(dst -> {
                    if (!visited.contains(dst)) {
                        toVisit.add(new AbstractMap.SimpleEntry<>(dst, currentNode.getValue() + 1));
                        visited.add(dst);
                    }
                });
            }
        } while (!toVisit.isEmpty());

        return ret;
    }

    /**
     * Calculates a set of the most influential nodes using the Degree Greedy algorithm.
     * @param k the maximum number of most influential nodes to return
     * @return a subset of the most k influential nodes
     */
    public ArrayList<String> mostInfluentialDegree(int k) {
        // Stores the top k influential nodes by the out degree
        final TreeSet<Map.Entry<String, Integer>> topK = new TreeSet<>(new NodeInfluenceComparator<>());
        // ArrayList to put the final output in
        ArrayList<String> ret = new ArrayList<>(k);

        // Iterate over every known node
        nodes.forEach(node -> {
            // Put the node and its out degree in the top list
            topK.add(new AbstractMap.SimpleEntry<>(node, outDegree(node)));

            // Trim the top list down to k elements
            while (topK.size() > k) {
                topK.remove(topK.first());
            }
        });

        // Remove the influence (out degree)
        topK.forEach(nodeAndInfluence -> ret.add(nodeAndInfluence.getKey()));
        return ret;
    }

    /**
     * Calculates a set of the most influential nodes using the Modular Greedy algorithm.
     * @param k the maximum number of most influential nodes to return
     * @return a subset of the most k influential nodes
     */
    public ArrayList<String> mostInfluentialModular(int k) {
        // Stores the top k influential nodes by the influence equation
        final TreeSet<Map.Entry<String, Float>> topK = new TreeSet<>(new NodeInfluenceComparator<>());
        // ArrayList to put the final output in
        ArrayList<String> ret = new ArrayList<>();

        // Iterate over every known node
        nodes.forEach(node -> {
            // Put the node and its influence in the top list
            topK.add(new AbstractMap.SimpleEntry<>(node, influence(node)));

            // Trim the top list down to k elements
            while (topK.size() > k) {
                topK.remove(topK.first());
            }
        });

        // Remove the influence
        topK.forEach(nodeAndInfluence -> ret.add(nodeAndInfluence.getKey()));
        return ret;
    }

    /**
     * Calculates a set of the most influential nodes using the SubModular Greedy algorithm.
     * @param k the maximum number of most influential nodes to return
     * @return a subset of the most k influential nodes
     */
    public ArrayList<String> mostInfluentialSubModular(int k) {
        // Store an array list for easy swapping out of elements
        ArrayList<String> S = new ArrayList<>(k);
        // Store the same data in a hash set for fast lookup
        HashSet<String> SHashSet = new HashSet<>();

        for (int i = 0; i < k; i++) {
            // Used to keep track of the most influential addition to the set
            String topNewNode = null;
            float topNewNodeInf = Float.NEGATIVE_INFINITY;
            // Keep track of the last element of the set, where v will be put
            int lastIndex = S.size();

            // Add a placeholder for v to set S
            S.add(null);

            for (String node : nodes) {
                // Skip v if it exists in S already
                if (!SHashSet.contains(node)) {
                    // Set the node to calculate the set influence with
                    S.set(lastIndex, node);
                    // Calculate the influence of S \\union v
                    float testInfluence = influence(S);

                    // Keep track of the set that creates the most influence
                    if (testInfluence > topNewNodeInf) {
                        topNewNode = node;
                        topNewNodeInf = testInfluence;
                    }
                }
            }

            // Add v to set S
            S.set(lastIndex, topNewNode);
            SHashSet.add(topNewNode);
        }
        return S;
    }

    /**
     * Sorts nodes by the influence (and falls back to alphabetical on equal influences)
     */
    private static class NodeInfluenceComparator<K extends Comparable<K>> implements Comparator<Map.Entry<String, K>> {
        @Override
        public int compare(Map.Entry<String, K> o1, Map.Entry<String, K> o2) {
            if (!o1.getValue().equals(o2.getValue())) {
                return o1.getValue().compareTo(o2.getValue());
            }
            return o1.getKey().compareTo(o2.getKey());
        }
    }
}