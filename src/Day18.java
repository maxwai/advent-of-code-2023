import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day18 {

    public static void main(String[] args) throws IOException {
        List<Operation> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("inputs/Day18-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static List<Operation> parseInput(Stream<String> lines) {
        List<Operation> operations = new ArrayList<>();
        lines.forEach(line -> {
            String[] split = line.split(" ");
            operations.add(new Operation(Direction.fromString(split[0]), Integer.parseInt(split[1]), split[2].substring(2, 8)));
        });
        return operations;
    }

    public static long part1(List<Operation> operations) {
        List<long[]> points = convertToPoints(operations);

        // Gaußsche Trapezformel (Schnürsenkel-Schema)
        long det = 0;
        for (int i = 0; i < points.size(); i++) {
            long[] p2 = points.get(i);
            long[] p1 = points.get((i + 1) % points.size());
            det += p1[0] * p2[1] - p1[1] * p2[0] + Math.abs(p1[0] - p2[0]) + Math.abs(p1[1] - p2[1]);
        }
        // Satz von Pick
        return Math.abs(det) / 2 + 1;
    }

    public static long part2(List<Operation> operations) {
        return part1(new ArrayList<>(operations.stream()
                .map(operation -> new Operation(Direction.fromChar(operation.color.charAt(5)), Integer.parseInt(operation.color.substring(0, 5), 16), operation.color))
                .toList()));
    }

    public static List<long[]> convertToPoints(List<Operation> operations) {
        List<long[]> result = new ArrayList<>();
        result.add(new long[]{0, 0});
        for (Operation operation : operations) {
            int dx, dy;
            dx = dy = 0;
            switch (operation.direction) {
                case UP -> dx = -1;
                case DOWN -> dx = 1;
                case LEFT -> dy = -1;
                case RIGHT -> dy = 1;
            }
            long x = result.get(result.size() - 1)[0] + dx * operation.amount;
            long y = result.get(result.size() - 1)[1] + dy * operation.amount;
            result.add(new long[]{x, y});
        }
        return result;
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT;

        public static Direction fromString(String direction) {
            return switch (direction) {
                case "U" -> UP;
                case "D" -> DOWN;
                case "R" -> RIGHT;
                case "L" -> LEFT;
                default -> throw new IllegalStateException();
            };
        }

        public static Direction fromChar(char direction) {
            return switch (direction) {
                case '3' -> UP;
                case '1' -> DOWN;
                case '0' -> RIGHT;
                case '2' -> LEFT;
                default -> throw new IllegalStateException();
            };
        }
    }

    public record Operation(Direction direction, int amount, String color) {
    }
}
