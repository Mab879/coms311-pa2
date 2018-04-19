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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        // implementation

        // replace this:
        return null;
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
     * @return the minimum distance between any node in s to node v
     */
    public int distance(ArrayList<String> s, String v) {
        // implementation

        // replace this:
        return -1;
    }

    /**
     * The influence of the node calculated by Sum[1/2^i * |{y|dist(x,y)==i}|, {i, 0, n}]
     * @param u the node to calculate the influence of
     * @return the decimal value of the influence of the node on the graph
     */
    public float influence(String u) {
        // implementation

        // replace this:
        return -1f;
    }

    /**
     * The influence of the subset on the set, calculated by Sum[1/2^i * |{y|dist(S,y)==i}|, {i, 0, n}]
     * @param s the subset of nodes to find the influence of
     * @return the decimal value of the influence of the subset of nodes on the graph
     */
    public float influence(ArrayList<String> s) {
        // implementation

        // replace this:
        return -1f;
    }

    /**
     * Calculates a set of the most influential nodes using the Degree Greedy algorithm.
     * @param k the maximum number of most influential nodes to return
     * @return a subset of the most k influential nodes
     */
    public ArrayList<String> mostInfluentialDegree(int k) {
        // implementation

        // replace this:
        return null;
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
}