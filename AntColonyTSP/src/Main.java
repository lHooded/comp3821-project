import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Main {
    public static final int mAnts = 10;
    public static final int nGens = 1000;

    public static void main(String[] args) {
        Graph graph = GraphFactory.RandomCompleteGraph(100, 1, 10);
        graph.initPheromone();

        double bestWeightSoFar = 1000000000;

        for (int gen_i = 0; gen_i < nGens; ++gen_i) {
            List<Ant> ants = new ArrayList<>();
            for (int k = 0; k < mAnts; ++k) {
                ants.add(new Ant(graph.getRandomVertex()));
            }

            for (int j = 0; j < graph.size; ++j) {
                ants.forEach(a -> a.move(graph));
            }

            // global updating
            Ant bestAnt = ants.stream().min(Comparator.comparingDouble(Ant::getTravelledDistance)).get();
            bestAnt.getTravelled().forEach(e -> e.setPheromoneStrength(e.getPheromoneStrength() + Ant.alpha / bestAnt.getTravelledDistance()));
            bestWeightSoFar = Math.min(bestWeightSoFar, bestAnt.getTravelledDistance());
            System.out.println(bestWeightSoFar);
        }
    }
}