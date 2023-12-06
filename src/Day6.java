import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Day6 {

    public static void main(String[] args) throws IOException {
        List<Integer[]> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day6-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static List<Integer[]> parseInput(Stream<String> lines) {
        List<Integer> times = new ArrayList<>();
        List<Integer> distances = new ArrayList<>();
        lines.forEach(line -> {
            Stream<Integer> stream = Arrays.stream(line.split(":")[1].trim().split(" "))
                    .filter(s -> !s.isEmpty())
                    .map(String::trim)
                    .map(Integer::parseInt);
            if (line.startsWith("Time:")) {
                stream.forEach(times::add);
            } else {
                stream.forEach(distances::add);
            }
        });
        List<Integer[]> races = new ArrayList<>();
        for (int i = 0; i < times.size(); i++) {
            races.add(new Integer[]{times.get(i), distances.get(i)});
        }
        return races;
    }

    public static long part1(List<Integer[]> races) {
        return races.stream()
                .mapToLong(race -> IntStream.range(0, race[0] + 1)
                        .filter(i -> i * (race[0] - i) > race[1])
                        .count())
                .reduce(1, (a, b) -> a * b);
    }

    public static long part2(List<Integer[]> races) {
        long time = Long.parseLong(races.stream()
                .map(race -> race[0])
                .map(String::valueOf)
                .reduce("", (s, s2) -> s + s2));
        long distance = Long.parseLong(races.stream()
                .map(race -> race[1])
                .map(String::valueOf)
                .reduce("", (s, s2) -> s + s2));
        return LongStream.range(0, time + 1)
                .filter(i -> i * (time - i) > distance)
                .count();
    }
}
