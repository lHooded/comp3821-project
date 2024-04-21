import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class Graph {
    public static final double ALPHA = 0.1;
    public static final double BETA = 2;
    public static final double Q_0 = 0.925;
    private double tau = 1/(100 * 24848.02898705297); // 1/(num cities * nearest neighbour heuristic)

    private final int numCities;
    private final City[] cities;
    private final double[][] weights;
    private final double[][] processedWeights;
    private final double[][] pheromoneStrength;

    public Graph(String inputFilename, int numCities) {
        this.numCities = numCities;
        weights = new double[numCities][numCities];
        processedWeights = new double[numCities][numCities];
        pheromoneStrength = new double[numCities][numCities];
        cities = new City[numCities];

        try {
            URL url = Graph.class.getResource(inputFilename);
            File input = new File(url.getPath());
            Scanner reader = new Scanner(input);

            int i = 0;
            while(reader.hasNextLine()) {
                String[] parts = reader.nextLine().split(" ");
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                cities[i] = new City(x, y, i);
                ++i;
            }

            for (i = 0; i < numCities; ++i) {
                for (int j = i + 1; j < numCities; ++j) {
                    weights[i][j] = Math.hypot(cities[j].x() - cities[i].x(), cities[j].y() - cities[i].y());
                    processedWeights[i][j] = Math.pow(1.0/weights[i][j], BETA);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(1);
        }
    }

    public void initPheromones() {
        for (int i = 0; i < numCities; ++i) {
            for (int j = i + 1; j < numCities; ++j) {
                pheromoneStrength[i][j] = getTau();
            }
        }
    }

    public void updatePheromone(double multiplied, double added, City a, City b) {
        if (a == null || b == null) {
            return;
        }
        int smaller = Math.min(a.id, b.id);
        int larger = Math.max(a.id, b.id);
        pheromoneStrength[smaller][larger] *= multiplied;
        pheromoneStrength[smaller][larger] += added;
    }

    public City getRandomCity() {
        return cities[new Random().nextInt(numCities)];
    }

    public double getTau() {
        // could do nearest neighbour heuristic here
        return tau;
    }

    public void run(int numAnts, int numGens, int numRuns) {
        double sum = 0;

        for (int run_i = 0; run_i < numRuns; ++run_i) {
            initPheromones();

            double bestWeightSoFar = 1000000000;
            Ant bestAntSoFar = null;

            for (int gen_j = 0; gen_j < numGens; ++gen_j) {
                Ant[] ants = new Ant[numAnts];
                for (int k = 0; k < numAnts; ++k) {
                    ants[k] = new Ant(getRandomCity());

                    for (int l = 0; l < numCities - 1; ++l) {
                        ants[k].move();
                    }
                }

                Ant bestAnt = Arrays.stream(ants).min(Comparator.comparingDouble(Ant::getDistanceTravelled)).get();

                bestAnt.globalUpdate();

                if (bestAnt.getDistanceTravelled() < bestWeightSoFar) {
                    bestWeightSoFar = bestAnt.getDistanceTravelled();
                    bestAntSoFar = bestAnt;
                }
                bestWeightSoFar = Math.min(bestWeightSoFar, bestAnt.getDistanceTravelled());
            }
            System.out.println(run_i + ": " + bestWeightSoFar);
            assert bestAntSoFar != null;
//            bestAntSoFar.printPATH();
            sum += bestWeightSoFar;
        }
        sum /= numRuns;
        System.out.println(sum);
    }

    public class City {
        private final double x;
        private final double y;
        private final int id;
        public City(double x, double y, int name) {
            this.x = x;
            this.y = y;
            this.id = name;
        }

        public double x() {
            return x;
        }

        public double y() {
            return y;
        }

        public double getDesirabilityTo(City other) {
            if (other == null) {
                return 0;
            }
            int smaller = Math.min(id, other.id);
            int larger = Math.max(id, other.id);
            return processedWeights[smaller][larger] * pheromoneStrength[smaller][larger];
        }

        public double getDistanceTo(City other) {
            int smaller = Math.min(id, other.id);
            int larger = Math.max(id, other.id);
            return weights[smaller][larger];
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            City city = (City) o;
            return Objects.equals(id, city.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "" + id;
        }
    }


    public class Ant {
        private LinkedHashSet<City> visited = new LinkedHashSet<>();
        private City pos;
        private final City startPos;
        private double totalWeight = 0;
        private boolean closedCycle = false;

        public Ant(City pos) {
            this.pos = pos;
            startPos = pos;
            visited.add(pos);
        }

        public void move() {
            Map<City, Double> desireabilities = new LinkedHashMap<>();
            for (int i = 0; i < numCities; ++i) {
                City c = cities[i];
                if (!visited.contains(c)) {
                    desireabilities.put(c, c.getDesirabilityTo(pos));
                }
            }

            City next;

            if (new Random().nextDouble() < Q_0) {
                next = desireabilities.keySet().stream().max(Comparator.comparingDouble(desireabilities::get)).orElse(null);
            } else {
                RandomCollection<City> choices = new RandomCollection<>();
                desireabilities.keySet().forEach(c -> {
                    choices.add(c.getDesirabilityTo(pos), c);
                });
                next = choices.next();
            }

            assert next != null;

            visited.add(next);
            totalWeight += pos.getDistanceTo(next);

            // local updating
            updatePheromone(1 - ALPHA, ALPHA * getTau(), pos, next);

            pos = next;
        }

        public double getDistanceTravelled() {
            if (!closedCycle) {
                closedCycle = true;
                totalWeight += pos.getDistanceTo(startPos);
            }
            return totalWeight;
        }

        public void globalUpdate() {
            City prev = null;
            for (City city : visited) {
                updatePheromone(1 - ALPHA, ALPHA / getDistanceTravelled(), prev, city);
                prev = city;
            }
            updatePheromone(1 - ALPHA, ALPHA / getDistanceTravelled(), pos, startPos);
        }

        public void printPATH() {
            visited.forEach(c -> System.out.print((c.id + 1) + ", "));
            System.out.println();
        }
    }
}
