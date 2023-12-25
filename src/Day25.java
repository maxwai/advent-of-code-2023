import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class Day25 {

    public static void main(String[] args) throws IOException {
        Map<String, List<String>> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("inputs/Day25-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
    }

    public static Map<String, List<String>> parseInput(Stream<String> lines) {
        Map<String, List<String>> connections = new HashMap<>();
        lines.forEach(line -> {
            String[] split = line.split(": ");
            String[] goals = split[1].split(" ");
            connections.putIfAbsent(split[0], new ArrayList<>());
            connections.get(split[0]).addAll(Arrays.stream(goals).toList());
            Arrays.stream(goals).forEach(connection -> {
                connections.putIfAbsent(connection, new ArrayList<>());
                connections.get(connection).add(split[0]);
            });
        });
        return connections;
    }

    public static long part1(Map<String, List<String>> connections) {
        // after looking at the graphviz representation, we need to remove xvh/dhn, ptj/qmr and lsv/lxt
        connections.get("xvh").remove("dhn");
        connections.get("dhn").remove("xvh");
        connections.get("ptj").remove("qmr");
        connections.get("qmr").remove("ptj");
        connections.get("lsv").remove("lxt");
        connections.get("lxt").remove("lsv");
        Set<String> cluster1 = new HashSet<>();
        cluster1.add("xvh");
        Set<String> cluster2 = new HashSet<>();
        cluster2.add("dhn");
        while (!connections.isEmpty()) {
            cluster1.addAll(cluster1.stream()
                    .filter(connections::containsKey)
                    .flatMap(node -> connections.remove(node).stream())
                    .toList());
            cluster2.addAll(cluster2.stream()
                    .filter(connections::containsKey)
                    .flatMap(node -> connections.remove(node).stream())
                    .toList());
        }
        return (long) cluster1.size() * cluster2.size();
    }
}
