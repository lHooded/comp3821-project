import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
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

    public static Graph CoordinatesToCompleteGraph(String inputFilename) {
        Graph graph = new Graph();

        try {
            URL url = GraphFactory.class.getResource(inputFilename);
            File input = new File(url.getPath());
            Scanner reader = new Scanner(input);

            int i = 1;
            while(reader.hasNextLine()) {
                String data = reader.nextLine().replaceAll("\\s","");
                String[] parts = data.split(",");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                graph.addVertex(new PositionalVertex(i, x, y));
                ++i;
            }

            int size = i - 1;
            List<Vertex> vertices = graph.getVertices();

            for (i = 0; i < size; ++i) {
                for (int j = i + 1; j < size; ++j) {
                    graph.addEdge(new Edge((PositionalVertex) vertices.get(i), (PositionalVertex) vertices.get(j)));
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return null;
        }

        return graph;
    }
}
