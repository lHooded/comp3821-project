import java.util.*;

public class Ant {
    public final static double decisiveness = 0.9; // q_0
    public final static double alpha = 0.1;
    private HashSet<Vertex> visited = new HashSet<>();
    private HashSet<Edge> travelled = new HashSet<>();
    private double totalWeight = 0;
    private Vertex pos;

    public Ant(Vertex pos) {
        this.pos = pos;
    }

    public boolean move(Graph graph) {
        Map<Edge, Double> desireabilities = new HashMap<>();
        double sum = graph.getAdjacentEdges(pos).stream()
                            .filter(e -> !visited.contains(e.getOtherVertex(pos)))
                            .peek(e -> desireabilities.put(e, e.getDesirability()))
                            .mapToDouble(Edge::getDesirability)
                            .sum();
        Edge nextTravelled = null;

        if (new Random().nextDouble() < decisiveness) {
            nextTravelled = desireabilities.keySet()
                                           .stream()
                                           .max(Comparator.comparingDouble(desireabilities::get))
                                           .orElse(null);
        } else {
            RandomCollection<Edge> probabilities = new RandomCollection<>();
            desireabilities.keySet().forEach(e -> probabilities.add(e.getDesirability() / sum, e));
            nextTravelled = probabilities.next();
        }

        if (nextTravelled == null) {
            return false;
        }

        Vertex nextPos = nextTravelled.getOtherVertex(pos);
        visited.add(nextPos);
        travelled.add(nextTravelled);
        pos = nextPos;

        totalWeight += nextTravelled.getWeight();

        // local updating
        nextTravelled.setPheromoneStrength((1 - alpha) * nextTravelled.getPheromoneStrength() + alpha * graph.getTau());

        return true;
    }

    public HashSet<Edge> getTravelled() {
        return travelled;
    }

    public double getTravelledDistance() {
        return totalWeight;
    }
}
