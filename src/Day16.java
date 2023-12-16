import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day16 {

    public static void main(String[] args) throws IOException {
        List<StringBuilder> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("inputs/Day16-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static List<StringBuilder> parseInput(Stream<String> lines) {
        List<StringBuilder> contraption = new ArrayList<>();
        lines.forEach(str -> contraption.add(new StringBuilder(str)));
        return contraption;
    }

    public static long part1(List<StringBuilder> contraption) {
        return tryFromLocation(contraption, 0, -1, 1);
    }

    public static long part2(List<StringBuilder> contraption) {
        return IntStream.range(0, contraption.size())
                .mapToLong(i -> Math.max(tryFromLocation(contraption, -1, i, 3),
                        Math.max(tryFromLocation(contraption, contraption.size(), i, 2),
                                Math.max(tryFromLocation(contraption, i, -1, 1),
                                        tryFromLocation(contraption, i, contraption.size(), 0)))))
                .max()
                .orElse(-1);
    }

    private static long tryFromLocation(List<StringBuilder> contraption, int x, int y, int dir) {
        Set<Beam> beams = new HashSet<>();
        Set<Beam> seen = new HashSet<>();
        Boolean[][] energized = new Boolean[contraption.size()][contraption.get(0).length()];
        beams.add(new Beam(x, y, dir));
        while (!beams.isEmpty()) {
            beams = helper(contraption, energized, beams, seen);
        }
        return totalEnergized(energized);
    }

    private static long totalEnergized(Boolean[][] energized) {
        return Arrays.stream(energized)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .filter(b -> b)
                .count();
    }

    private static Set<Beam> helper(List<StringBuilder> contraption, Boolean[][] energized, Set<Beam> beams, Set<Beam> seen) {
        Set<Beam> newBeams = new HashSet<>();
        int[] xs = new int[]{0, 0, -1, 1};
        int[] ys = new int[]{-1, 1, 0, 0};
        for (Beam beam : beams) {
            int dir = beam.direction;
            int newx = beam.x + xs[dir];
            int newy = beam.y + ys[dir];
            if (newx < 0 || newx >= contraption.size() || newy < 0 || newy >= contraption.get(0).length()) {
                continue;
            }
            energized[newx][newy] = true;
            switch (contraption.get(newx).charAt(newy)) {
                case '.' -> newBeams.add(new Beam(newx, newy, dir));
                case '/' -> newBeams.add(new Beam(newx, newy, 3 - dir));
                case '\\' -> newBeams.add(new Beam(newx, newy, (dir + 2) % 4));
                case '|' -> {
                    if (dir >= 2) {
                        newBeams.add(new Beam(newx, newy, dir));
                    } else {
                        newBeams.add(new Beam(newx, newy, 2));
                        newBeams.add(new Beam(newx, newy, 3));
                    }
                }
                case '-' -> {
                    if (dir < 2) {
                        newBeams.add(new Beam(newx, newy, dir));
                    } else {
                        newBeams.add(new Beam(newx, newy, 0));
                        newBeams.add(new Beam(newx, newy, 1));
                    }
                }
            }
        }
        newBeams.removeAll(seen);
        seen.addAll(newBeams);
        return newBeams;
    }

    public record Beam(int x, int y, int direction) {
    }
}
