import java.util.Objects;

public class Edge {
    public static final double WEIGHT_POWER = 2;
    private final double weight;
    private final double processedWeight;
    private double pheromoneStrength;

    private final Vertex a;
    private final Vertex b;

    public Edge(double weight, Vertex a, Vertex b) {
        this.weight = weight;
        this.processedWeight = Math.pow(1.0/weight, WEIGHT_POWER);
        this.a = a;
        this.b = b;
    }

    public double getWeight() {
        return weight;
    }

    public double getPheromoneStrength() {
        return pheromoneStrength;
    }

    public void setPheromoneStrength(double pheromoneStrength) {
        this.pheromoneStrength = pheromoneStrength;
    }

    public Vertex getA() {
        return a;
    }

    public Vertex getB() {
        return b;
    }

    public Vertex getOtherVertex(Vertex v) {
        return v == a ? b : a;
    }

    public double getDesirability() {
        return pheromoneStrength * processedWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(a, edge.a) && Objects.equals(b, edge.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
