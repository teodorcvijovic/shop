package student;

import java.util.HashMap;
import java.util.List;

public class CityGraph {

    private static CityGraph graph = null;

    private CityGraph() {
        // TO DO: create matrix from DB
    }

    public static CityGraph getInstance() {
        if (graph == null) {
            graph = new CityGraph();
        }
        return graph;
    }

    // TO DO: shortest path between two cities

    /************ save path for every active order ***************/

    static class Node implements Comparable<Node> {
        int cityId;
        int distance;

        public Node(int cityId, int distance) {
            this.cityId = cityId;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    public HashMap<Integer, List<Node>> pathForActiveOrderMap = new HashMap<Integer, List<Node>>();

    // TO DO: save and delete path methods

    /***********************************************************/

    public static void main(String[] args) {
        // TO DO: test this class
    }
}
