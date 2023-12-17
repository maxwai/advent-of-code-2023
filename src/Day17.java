import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class Day17 {

    public static void main(String[] args) throws IOException {
        Graph input;
        try (BufferedReader reader = new BufferedReader(new FileReader("inputs/Day17-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static Graph parseInput(Stream<String> lines) {
        int x = 0;
        Map<Node, Node> nodes = new HashMap<>();
        Iterator<String> linesIterator = lines.iterator();
        while (linesIterator.hasNext()) {
            char[] line = linesIterator.next().toCharArray();
            for (int y = 0; y < line.length; y++) {
                Node node = new Node(x, y, Integer.parseInt("" + line[y]));
                nodes.put(node, node);
            }
            x++;
        }

        nodes.forEach((node, node1) -> {
            Optional.ofNullable(nodes.get(new Node(node.x - 1, node.y, 0)))
                    .ifPresent(neighbour -> {
                        new Edge(node, neighbour, neighbour.cost, Direction.UP);
                        new Edge(neighbour, node, node.cost, Direction.DOWN);
                    });
            Optional.ofNullable(nodes.get(new Node(node.x + 1, node.y, 0)))
                    .ifPresent(neighbour -> {
                        new Edge(node, neighbour, neighbour.cost, Direction.DOWN);
                        new Edge(neighbour, node, node.cost, Direction.UP);
                    });
            Optional.ofNullable(nodes.get(new Node(node.x, node.y - 1, 0)))
                    .ifPresent(neighbour -> {
                        new Edge(node, neighbour, neighbour.cost, Direction.LEFT);
                        new Edge(neighbour, node, node.cost, Direction.RIGHT);
                    });
            Optional.ofNullable(nodes.get(new Node(node.x, node.y + 1, 0)))
                    .ifPresent(neighbour -> {
                        new Edge(node, neighbour, neighbour.cost, Direction.RIGHT);
                        new Edge(neighbour, node, node.cost, Direction.LEFT);
                    });
        });

        int nodeCount = nodes.size();

        return new Graph(nodes.get(new Node(0, 0, 0)),
                nodes.get(new Node((int) Math.sqrt(nodeCount) - 1, (int) Math.sqrt(nodeCount) - 1, 0)),
                nodeCount);
    }

    public static long part1(Graph graph) {
        return getCost(graph, a_star(graph, 0, 3));
    }

    public static long part2(Graph graph) {
        return getCost(graph, a_star(graph, 4, 10));
    }

    public static int getCost(Graph graph, AStarResult aStarResult) {
        if (aStarResult == null)
            return -1;
        return aStarResult.costMap.entrySet()
                .stream()
                .filter(n -> n.getKey().node.equals(graph.stop))
                .min(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getValue)
                .orElseThrow();

    }

    public static AStarResult a_star(Graph graph, int minStraight, int maxStraight) {
        Map<AStarNode, Integer> costMap = new HashMap<>();
        Map<AStarNode, Edge> previousMap = new HashMap<>();
        Map<AStarNode, AStarNode> paths = new HashMap<>();
        PriorityQueue<AStarNode> queue = new PriorityQueue<>(Comparator.comparingInt(costMap::get));
        AStarNode aStarNode = new AStarNode(graph.start, null, 1);
        queue.add(aStarNode);
        costMap.put(aStarNode, 0);
        while (!queue.isEmpty()) {
            AStarNode currentNode = queue.remove();
            if (currentNode.node.equals(graph.stop)) {
                return new AStarResult(costMap, previousMap, paths);
            }
            for (Edge outgoingEdge : currentNode.node.outgoingEdges) {
                if ((outgoingEdge.direction == currentNode.direction && currentNode.straightCount == maxStraight) ||
                    (currentNode.direction != null && outgoingEdge.direction != currentNode.direction && currentNode.straightCount < minStraight) ||
                    (currentNode.direction != null && outgoingEdge.direction.reversed(currentNode.direction))) {
                    continue;
                }

                aStarNode = new AStarNode(outgoingEdge.to,
                        outgoingEdge.direction,
                        outgoingEdge.direction == currentNode.direction ? currentNode.straightCount + 1 : 1);

                if (costMap.getOrDefault(aStarNode, Integer.MAX_VALUE) > costMap.get(currentNode) + outgoingEdge.cost) {
                    queue.remove(aStarNode);
                    costMap.put(aStarNode, costMap.get(currentNode) + outgoingEdge.cost);
                    previousMap.put(aStarNode, outgoingEdge);
                    queue.add(aStarNode);
                    paths.put(aStarNode, currentNode);
                }
            }

        }
        return null;
    }

    public enum Direction {
        DOWN, UP, LEFT, RIGHT;

        public boolean reversed(Direction direction) {
            return switch (this) {
                case DOWN -> direction == UP;
                case UP -> direction == DOWN;
                case LEFT -> direction == RIGHT;
                case RIGHT -> direction == LEFT;
            };
        }
    }

    public record AStarResult(Map<AStarNode, Integer> costMap, Map<AStarNode, Edge> previousMap, Map<AStarNode, AStarNode> paths) {
    }

    public record Graph(Node start, Node stop, int nodeCount) {
    }

    public record Edge(Node from, Node to, int cost, Direction direction) {

        public Edge(Node from, Node to, int cost, Direction direction) {
            this.from = from;
            this.to = to;
            this.cost = cost;
            this.direction = direction;
            this.from.outgoingEdges.add(this);
            this.to.incomingEdges.add(this);
        }
    }

    public static class Node {

        public final int x, y, cost;

        public final List<Edge> outgoingEdges = new ArrayList<>();

        public final List<Edge> incomingEdges = new ArrayList<>();

        public Node(int x, int y, int cost) {
            this.x = x;
            this.y = y;
            this.cost = cost;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof Node other)
                return x == other.x && y == other.y;
            return false;
        }

        @Override
        public String toString() {
            return "Node{" +
                   "x=" + x +
                   ", y=" + y +
                   ", cost=" + cost +
                   '}';
        }
    }

    public record AStarNode(Node node, Direction direction, int straightCount) {
    }
}
