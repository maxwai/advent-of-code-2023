import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Day8 {

    public static void main(String[] args) throws IOException {
        Map<String, Tree> input;
        List<Character> path;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day8-input.txt"))) {
            path = new ArrayList<>(reader.readLine()
                    .chars()
                    .mapToObj(m -> (char) m)
                    .toList());
            reader.readLine();
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input, path));
        System.out.println(part2(input, path));
    }

    public static Map<String, Tree> parseInput(Stream<String> lines) {
        Map<String, List<String>> mappings = new HashMap<>();
        lines.forEach(line -> {
            String[] split = line.split("=");
            String[] value = split[1].trim().substring(1, 9).split(",");
            mappings.put(split[0].trim(), List.of(value[0].trim(), value[1].trim()));
        });
        return Tree.buildTree(mappings);
    }

    public static long part1(Map<String, Tree> trees, List<Character> path) {
        Tree currentTree = trees.get("AAA");
        int steps = 0;
        //noinspection DataFlowIssue
        while (!currentTree.name.equals("ZZZ")) {
            steps++;
            char currentPath = path.remove(0);
            path.add(currentPath);
            currentTree = switch (currentPath) {
                case 'L' -> currentTree.left;
                case 'R' -> currentTree.right;
                default -> null;
            };
        }
        return steps;
    }

    public static long part2(Map<String, Tree> trees, List<Character> path) {
        return trees.entrySet()
                .stream()
                .filter(entry -> entry.getKey().endsWith("A"))
                .map(entry -> {
                    List<Character> localPath = new ArrayList<>(path);
                    Tree currentTree = entry.getValue();
                    int steps = 0;
                    //noinspection DataFlowIssue
                    while (!currentTree.name.endsWith("Z")) {
                        steps++;
                        char currentPath = localPath.remove(0);
                        localPath.add(currentPath);
                        currentTree = switch (currentPath) {
                            case 'L' -> currentTree.left;
                            case 'R' -> currentTree.right;
                            default -> null;
                        };
                    }
                    return steps;
                })
                .map(BigInteger::valueOf)
                .reduce(BigInteger.ONE, (a, b) -> {
                    BigInteger gcd = a.gcd(b);
                    BigInteger absProduct = a.multiply(b).abs();
                    return absProduct.divide(gcd);
                }).longValue();
    }

    public static class Tree {
        public final String name;
        public Tree left, right;

        public Tree(String name) {
            this.name = name;
        }

        public static Map<String, Tree> buildTree(Map<String, List<String>> mappings) {
            Map<String, Tree> trees = new HashMap<>();
            mappings.forEach((key, value) -> {
                Tree newTree = trees.getOrDefault(key, new Tree(key));
                trees.putIfAbsent(key, newTree);
                newTree.left = trees.getOrDefault(value.get(0), new Tree(value.get(0)));
                trees.putIfAbsent(value.get(0), newTree.left);
                newTree.right = trees.getOrDefault(value.get(1), new Tree(value.get(1)));
                trees.putIfAbsent(value.get(1), newTree.right);
            });
            return trees;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Tree other) {
                return name.equals(other.name);
            }
            return false;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
