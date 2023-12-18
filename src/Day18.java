import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
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
        List<List<Character>> field = new ArrayList<>();
        field.add(new ArrayList<>());
        int x = 0;
        int y = 0;
        field.get(x).add(y, '#');
        for (Operation operation : operations) {
            int dx, dy;
            dx = dy = 0;
            switch (operation.direction) {
                case UP -> dx = -1;
                case DOWN -> dx = 1;
                case LEFT -> dy = -1;
                case RIGHT -> dy = 1;
            }
            for (int i = 0; i < operation.amount; i++) {
                x += dx;
                y += dy;
                if (x < 0) {
                    field.add(0, new ArrayList<>());
                    x++;
                } else if (x >= field.size()) {
                    field.add(new ArrayList<>());
                }
                if (y < 0) {
                    field.forEach(line -> line.add(0, ' '));
                    y++;
                }
                while (y >= field.get(x).size()) {
                    field.forEach(line -> line.add(' '));
                }
                field.get(x).set(y, '#');
            }
        }
        int fieldLength = field.stream()
                .mapToInt(List::size)
                .max()
                .orElseThrow();

        field.get(1).set(75, '.');

        boolean changed;
        do {
            changed = false;
            for (int i = 0; i < field.size(); i++) {
                for (int j = 0; j < field.get(i).size(); j++) {
                    if (field.get(i).get(j) == '.') {
                        for (x = -1; x <= 1; x += 2) {
                            if (i + x >= 0 && i + x < field.size() && field.get(i + x).get(j) == ' ') {
                                field.get(i + x).set(j, '.');
                                changed = true;
                            }
                            if (j + x >= 0 && j + x < field.get(i).size() && field.get(i).get(j + x) == ' ') {
                                field.get(i).set(j + x, '.');
                                changed = true;
                            }
                        }
                    }
                }
            }
        } while (changed);

        field.stream()
                .map(line -> line.stream()
                        .map(ch -> "" + ch)
                        .reduce("", (s, character) -> s + character))
                .forEach(System.out::println);
        return 0;
    }

    public static long part2(List<Operation> operations) {
        return 0;
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
    }

    public record Operation(Direction direction, int amount, String color) {
    }
}
