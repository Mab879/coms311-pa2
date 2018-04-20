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
    private int vertexCount;
    private HashMap<String, List<String>> edges = new HashMap<>();

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
                // A stack used to reverse the back trace
                Stack<String> path = new Stack<>();

                // Navigate through the backlinks from v to u
                do {
                    path.push(backNode);
                    backNode = parent.get(backNode);
                } while (!backNode.equals(u));
                path.push(backNode);
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
        TreeMap<Integer, Integer> tieredCounts = tieredBfs(u);
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
        TreeMap<Integer, Integer> tieredCounts = tieredBfs(s);
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
    private TreeMap<Integer, Integer> tieredBfs(String startingNode) {
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
    private TreeMap<Integer, Integer> tieredBfs(Iterable<String> startingNodes) {
        // <depth, count of nodes in that depth>
        TreeMap<Integer, Integer> ret = new TreeMap<>();
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
        final TreeSet<Map.Entry<String, Integer>> topK = new TreeSet<>(new NodeOutDegreeComparator());
        // ArrayList to put the final output in
        ArrayList<String> ret = new ArrayList<>(k);

        // Iterate over every known node
        edges.forEach((node, outNodes) -> {
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
        // implementation

        // replace this:
        return null;
    }

    /**
     * Calculates a set of the most influential nodes using the SubModular Greedy algorithm.
     * @param k the maximum number of most influential nodes to return
     * @return a subset of the most k influential nodes
     */
    public ArrayList<String> mostInfluentialSubModular(int k) {
        // implementation

        // replace this:
        return null;
    }

    /**
     * Sorts nodes by the out degree (and falls back to alphabetical on equal out degrees)
     */
    private static class NodeOutDegreeComparator implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            if (!o1.getValue().equals(o2.getValue())) {
                return o1.getValue().compareTo(o2.getValue());
            }
            return o1.getKey().compareTo(o2.getKey());
        }
    }
}