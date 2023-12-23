import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class Day23 {

    public static void main(String[] args) throws IOException {
        List<Node> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("inputs/Day23-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static List<Node> parseInput(Stream<String> lines) {
        List<Node> nodes = new ArrayList<>();
        List<String> lineList = lines.toList();
        for (int y = 0; y < lineList.size(); y++) {
            for (int x = 0; x < lineList.get(y).length(); x++) {
                if (lineList.get(y).charAt(x) == '#')
                    continue;
                nodes.add(new Node(new Coords(x, y), lineList.get(y).charAt(x)));
            }
        }
        nodes.forEach(node -> {
            switch (node.ch) {
                case '>' -> node.neighbours.addAll(nodes.stream()
                        .filter(node1 -> node1.coords.equals(new Coords(node.coords.x + 1, node.coords.y)))
                        .toList());
                case '<' -> node.neighbours.addAll(nodes.stream()
                        .filter(node1 -> node1.coords.equals(new Coords(node.coords.x - 1, node.coords.y)))
                        .toList());
                case '^' -> node.neighbours.addAll(nodes.stream()
                        .filter(node1 -> node1.coords.equals(new Coords(node.coords.x, node.coords.y - 1)))
                        .toList());
                case 'v' -> node.neighbours.addAll(nodes.stream()
                        .filter(node1 -> node1.coords.equals(new Coords(node.coords.x, node.coords.y + 1)))
                        .toList());
                case '.' -> node.neighbours.addAll(nodes.stream()
                        .filter(node1 -> (node1.coords.equals(new Coords(node.coords.x + 1, node.coords.y)) && node1.ch != '<') ||
                                         (node1.coords.equals(new Coords(node.coords.x - 1, node.coords.y)) && node1.ch != '>') ||
                                         (node1.coords.equals(new Coords(node.coords.x, node.coords.y + 1)) && node1.ch != '^') ||
                                         (node1.coords.equals(new Coords(node.coords.x, node.coords.y - 1)) && node1.ch != 'v'))
                        .toList());
                default -> throw new IllegalStateException();
            }
        });

        return nodes;
    }

    public static long part1(List<Node> nodes) {
        Node start = nodes.get(0);
        Node goal = nodes.get(nodes.size() - 1);
        List<List<Node>> queue = new ArrayList<>();
        List<List<Node>> donePaths = Collections.synchronizedList(new ArrayList<>());
        queue.add(new ArrayList<>(Collections.singleton(start)));
        while (!queue.isEmpty()) {
            queue = queue.stream()
                    .parallel()
                    .flatMap(currentPath -> {
                        List<Node> neighbours = currentPath.get(currentPath.size() - 1).neighbours.stream()
                                .filter(node -> !currentPath.contains(node))
                                .toList();
                        return neighbours.stream()
                                .map(neighbour -> {
                                    List<Node> newPath = new ArrayList<>(currentPath);
                                    newPath.add(neighbour);
                                    if (neighbour.equals(goal)) {
                                        donePaths.add(newPath);
                                        return null;
                                    } else {
                                        return newPath;
                                    }
                                })
                                .filter(Objects::nonNull);
                    })
                    .toList();
        }
        System.out.println(donePaths.size());
        return donePaths.stream()
                .parallel()
                .mapToLong(nodes1 -> {
                    int nodesBetween = 0;
                    for (int i = 0; i < nodes1.size() - 1; i++) {
                        nodesBetween += nodes1.get(i).compactNodes.getOrDefault(nodes1.get(i+1), Collections.emptySet()).size();
                    }
                    return nodes1.size() + nodesBetween;
                })
                .max()
                .orElseThrow() - 1;
    }

    public static long part2(List<Node> nodes) {
        nodes.forEach(node -> {
            node.neighbours.clear();
            node.neighbours.addAll(nodes.stream()
                    .filter(node1 -> node1.coords.equals(new Coords(node.coords.x + 1, node.coords.y)) ||
                                     node1.coords.equals(new Coords(node.coords.x - 1, node.coords.y)) ||
                                     node1.coords.equals(new Coords(node.coords.x, node.coords.y + 1)) ||
                                     node1.coords.equals(new Coords(node.coords.x, node.coords.y - 1)))
                    .toList());
        });
        Node notNeededNode;
        do {
            notNeededNode = nodes.stream()
                    .filter(node -> node.neighbours.size() == 2)
                    .findAny()
                    .orElse(null);
            if (notNeededNode != null) {
                Node neighbour1 = notNeededNode.neighbours.get(0);
                Node neighbour2 = notNeededNode.neighbours.get(1);

                neighbour1.neighbours.remove(notNeededNode);
                neighbour1.neighbours.add(neighbour2);
                Set<Node> compactNode = neighbour1.compactNodes.remove(notNeededNode);
                if (compactNode == null) {
                    compactNode = new HashSet<>();
                }
                compactNode.add(notNeededNode);
                compactNode.addAll(notNeededNode.compactNodes.getOrDefault(neighbour2, Collections.emptySet()));
                neighbour1.compactNodes.put(neighbour2, compactNode);

                neighbour2.neighbours.remove(notNeededNode);
                neighbour2.neighbours.add(neighbour1);
                compactNode = neighbour2.compactNodes.remove(notNeededNode);
                if (compactNode == null) {
                    compactNode = new HashSet<>();
                }
                compactNode.add(notNeededNode);
                compactNode.addAll(notNeededNode.compactNodes.getOrDefault(neighbour1, Collections.emptySet()));
                neighbour2.compactNodes.put(neighbour1, compactNode);

                nodes.remove(notNeededNode);
            }
        } while (notNeededNode != null);
        return part1(nodes);
    }

    public record Coords(int x, int y) {
    }

    public static final class Node {
        public final Coords coords;
        public final char ch;
        public final List<Node> neighbours = new ArrayList<>();
        public final Map<Node, Set<Node>> compactNodes = new HashMap<>();

        public Node(Coords coords, char ch) {
            this.coords = coords;
            this.ch = ch;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Node) obj;
            return Objects.equals(this.coords, that.coords) &&
                   Objects.equals(this.ch, that.ch) &&
                   Objects.equals(this.neighbours, that.neighbours);
        }

        @Override
        public int hashCode() {
            return coords.hashCode();
        }
    }
}
