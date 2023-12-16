import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        return tryFromLocation(contraption, 0, -1, Direction.RIGHT);
    }

    public static long part2(List<StringBuilder> contraption) {
        return IntStream.range(0, contraption.size())
                .parallel()
                .mapToLong(i -> Math.max(tryFromLocation(contraption, -1, i, Direction.DOWN),
                        Math.max(tryFromLocation(contraption, contraption.size(), i, Direction.UP),
                                Math.max(tryFromLocation(contraption, i, -1, Direction.RIGHT),
                                        tryFromLocation(contraption, i, contraption.size(), Direction.LEFT)))))
                .max()
                .orElse(-1);
    }

    private static long tryFromLocation(List<StringBuilder> contraption, int x, int y, Direction dir) {
        Set<Beam> beams = new HashSet<>();
        Set<Beam> seen = new HashSet<>();
        Set<Coords> energized = new HashSet<>();
        beams.add(new Beam(new Coords(x, y), dir));
        while (!beams.isEmpty()) {
            Set<Beam> newBeams = new HashSet<>();
            for (Beam beam : beams) {
                int newx = beam.coords.x + beam.direction.xShift;
                int newy = beam.coords.y + beam.direction.yShift;
                Coords newCoords = new Coords(newx, newy);
                if (newx < 0 || newx >= contraption.size() || newy < 0 || newy >= contraption.get(0).length()) {
                    continue;
                }
                energized.add(newCoords);
                switch (contraption.get(newx).charAt(newy)) {
                    case '.' -> newBeams.add(new Beam(newCoords, beam.direction));
                    case '/' -> newBeams.add(new Beam(newCoords, beam.direction.mirror1()));
                    case '\\' -> newBeams.add(new Beam(newCoords, beam.direction.mirror2()));
                    case '|' -> {
                        if (beam.direction.vertical) {
                            newBeams.add(new Beam(newCoords, beam.direction));
                        } else {
                            newBeams.add(new Beam(newCoords, Direction.UP));
                            newBeams.add(new Beam(newCoords, Direction.DOWN));
                        }
                    }
                    case '-' -> {
                        if (beam.direction.horizontal) {
                            newBeams.add(new Beam(newCoords, beam.direction));
                        } else {
                            newBeams.add(new Beam(newCoords, Direction.LEFT));
                            newBeams.add(new Beam(newCoords, Direction.RIGHT));
                        }
                    }
                }
            }
            newBeams.removeAll(seen);
            seen.addAll(newBeams);
            beams = newBeams;
        }
        return energized.size();
    }

    public enum Direction {
        LEFT(0, -1, true, false),
        RIGHT(0, 1, true, false),
        UP(-1, 0, false, true),
        DOWN(1, 0, false, true);

        public final int xShift, yShift;
        public final boolean horizontal, vertical;

        Direction(int xShift, int yShift, boolean horizontal, boolean vertical) {
            this.xShift = xShift;
            this.yShift = yShift;
            this.horizontal = horizontal;
            this.vertical = vertical;
        }

        public Direction mirror1() {
            return switch (this) {
                case LEFT -> DOWN;
                case RIGHT -> UP;
                case UP -> RIGHT;
                case DOWN -> LEFT;
            };
        }

        public Direction mirror2() {
            return switch (this) {
                case LEFT -> UP;
                case RIGHT -> DOWN;
                case UP -> LEFT;
                case DOWN -> RIGHT;
            };
        }
    }

    public record Coords(int x, int y) {
    }

    public record Beam(Coords coords, Direction direction) {
    }
}
