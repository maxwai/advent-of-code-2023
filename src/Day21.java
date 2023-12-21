import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day21 {

    public static void main(String[] args) throws IOException {
        List<StringBuilder> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("inputs/Day21-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input, 64));
        System.out.println(part2(input));
    }

    public static List<StringBuilder> parseInput(Stream<String> lines) {
        List<StringBuilder> plot = new ArrayList<>();
        lines.forEach(line -> plot.add(new StringBuilder(line)));
        return plot;
    }

    public static long part1(List<StringBuilder> plot, int steps) {
        List<StringBuilder> finalPlot = plot.stream()
                .map(line -> new StringBuilder(line.toString()))
                .toList();
        Set<Coords> queue = new HashSet<>();
        outerLoop:
        for (int i = 0; i < finalPlot.size(); i++) {
            for (int j = 0; j < finalPlot.get(i).length(); j++) {
                if (finalPlot.get(i).charAt(j) == 'S') {
                    finalPlot.get(i).setCharAt(j, '.');
                    queue.add(new Coords(i, j));
                    break outerLoop;
                }
            }
        }

        for (int i = 0; i < steps; i++) {
            queue = queue.stream()
                    .flatMap(coords -> {
                        List<Coords> list = new ArrayList<>(4);
                        if (finalPlot.get((((coords.i - 1) % finalPlot.size()) + finalPlot.size()) % finalPlot.size()).charAt(((coords.j % finalPlot.size()) + finalPlot.size()) % finalPlot.size()) == '.') {
                            list.add(new Coords(coords.i - 1, coords.j));
                        }
                        if (finalPlot.get((((coords.i + 1) % finalPlot.size()) + finalPlot.size()) % finalPlot.size()).charAt(((coords.j % finalPlot.size()) + finalPlot.size()) % finalPlot.size()) == '.') {
                            list.add(new Coords(coords.i + 1, coords.j));
                        }
                        if (finalPlot.get(((coords.i % finalPlot.size()) + finalPlot.size()) % finalPlot.size()).charAt((((coords.j - 1) % finalPlot.size()) + finalPlot.size()) % finalPlot.size()) == '.') {
                            list.add(new Coords(coords.i, coords.j - 1));
                        }
                        if (finalPlot.get(((coords.i % finalPlot.size()) + finalPlot.size()) % finalPlot.size()).charAt((((coords.j + 1) % finalPlot.size()) + finalPlot.size()) % finalPlot.size()) == '.') {
                            list.add(new Coords(coords.i, coords.j + 1));
                        }
                        return list.stream();
                    })
                    .collect(Collectors.toSet());
        }
        return queue.size();
    }

    public static long part2(List<StringBuilder> plot) {
        // note that 26501365 = 202300 * 131 + 65 where 131 is the dimension of the grid
        // while looking at the spread when slowly increasing the steps, we can see that it progresses in a rhombus shape
        long a0 = part1(plot, 65);
        long a1 = part1(plot, 65 + 131);
        long a2 = part1(plot, 65 + 2 * 131);

        // since there are easy libraries for python, we solve in python
        long result;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "src/Day21.py", "" + a0, "" + a1, "" + a2, "202300");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            List<String> results = new BufferedReader(new InputStreamReader(process.getInputStream())).lines().toList();
            result = Long.parseLong(results.get(0));
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public record Coords(int i, int j) {
    }
}
