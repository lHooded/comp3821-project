import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GraphFactory {
    private static Edge RandomEdge(Vertex a, Vertex b, int minWeight, int maxWeight) {
        Random random = new Random();
        return new Edge(minWeight + random.nextDouble() * (maxWeight - minWeight), a, b);
    }

    public static Graph RandomCompleteGraph(int size, int minWeight, int maxWeight) {
        Graph graph = new Graph();
        for (int i = 0; i < size; ++i) {
            graph.addVertex(new Vertex(i));
        }

        List<Vertex> vertices = graph.getVertices();

        for (int i = 0; i < size; ++i) {
            for (int j = i + 1; j < size; ++j) {
                graph.addEdge(RandomEdge(vertices.get(i), vertices.get(j), minWeight, maxWeight));
            }
        }

        return graph;
    }
}
