import java.util.*;

public class Graph {
    public double tau = 0;
    public int size = 0;
    private HashMap<Vertex, HashSet<Edge>> adj = new HashMap<>();

    private List<Edge> tempEdgeList = new ArrayList<>();

    public boolean addVertex(Vertex v) {
        if (adj.containsKey(v)) {
            return false;
        }
        adj.put(v, new HashSet<>());
        size++;
        return true;
    }

    public boolean addEdge(Edge e) {
        Vertex a = e.getA();
        Vertex b = e.getB();
        if (!adj.containsKey(a) || !adj.containsKey(b)) {
            return false;
        }

        adj.get(a).add(e);
        adj.get(b).add(e);
        tempEdgeList.add(e);
        return true;
    }

    public void initPheromone() {
        tempEdgeList.forEach(e -> e.setPheromoneStrength(getTau()));
        tempEdgeList = new ArrayList<>();
    }

    public List<Edge> getAdjacentEdges(Vertex v) {
        if (!adj.containsKey(v)) {
            return null;
        }
        return new ArrayList<>(adj.get(v));
    }

    public Edge getEdge(Vertex a, Vertex b) {
        return getAdjacentEdges(a).stream().filter(e -> e.getOtherVertex(a) == b).findAny().orElse(null);
    }

    public List<Vertex> getVertices() {
        return new ArrayList<>(adj.keySet());
    }

    // assumes that there are vertices
    public Vertex getRandomVertex() {
        return adj.keySet().stream()
                .skip((int) (size * Math.random()))
                .findFirst().get();
    }
    // assumes the graph is connected and there is at least one edge
    public double getTau() {
        if (tau != 0) {
            return tau;
        }

        // Now we perform nearest neighbour heuristic
        HashSet<Vertex> visited = new HashSet<>();
//        Vertex start = adj.keySet().iterator().next();
        Vertex v = adj.keySet().iterator().next();
        double totalWeight = 0;
        for (int i = 0; i < size; ++i) {
            List<Edge> adjacentEdges = getAdjacentEdges(v);
            Vertex finalV = v;
            Edge nextEdge = adjacentEdges.stream().filter(e -> !visited.contains(e.getOtherVertex(finalV))).min(Comparator.comparingDouble(Edge::getWeight)).get();
            totalWeight += nextEdge.getWeight();
            v = nextEdge.getOtherVertex(v);
            visited.add(v);
        }

        tau = 1 / (size * totalWeight);
        return tau;
    }
}
