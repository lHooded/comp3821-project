public class Experiments {
    public static void kroA100() {
        Graph graph;
        System.out.println("With default parameters:");
        graph = new Graph("kroA100.data", 100, 0.1, 2, 0.9, 1.0/(100 * 25000));
        graph.run(10, 1000, 15);
        System.out.println("\n\n");

        System.out.println("Setting alpha to 0.925");
        graph = new Graph("kroA100.data", 100, 0.1, 2, 0.925, 1.0/(100 * 25000));
        graph.run(10, 1000, 15);
        System.out.println("\n\n");

        System.out.println("Setting beta to 4");
        graph = new Graph("kroA100.data", 100, 0.1, 4, 0.9, 1.0/(100 * 25000));
        graph.run(10, 1000, 15);
        System.out.println("\n\n");

        System.out.println("Setting alpha to 0.925 and beta to 4");
        graph = new Graph("kroA100.data", 100, 0.1, 4, 0.925, 1.0/(100 * 25000));
        graph.run(10, 1000, 15);
        System.out.println("\n\n");
    }
}
