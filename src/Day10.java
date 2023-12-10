import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day10 {

    public static void main(String[] args) throws IOException {
        List<Tile> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day10-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static List<Tile> parseInput(Stream<String> lines) {
        List<List<String>> pipes = new ArrayList<>();
        lines.forEach(line -> pipes.add(new ArrayList<>(Arrays.asList(line.split("")))));

        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < pipes.size(); i++) {
            for (int j = 0; j < pipes.get(i).size(); j++) {
                if (pipes.get(i).get(j).equals(".")) {
                    continue;
                }
                tiles.add(new Tile(i, j, pipes.get(i).get(j)));
            }
        }
        return tiles;
    }

    public static long part1(List<Tile> tiles) {
        List<Tile> localTiles = new ArrayList<>(tiles);

        Tile startingTile = localTiles.stream()
                                    .filter(tile -> tile.pipeDirection == PipeDirection.START)
                                    .findAny()
                                    .orElseThrow();

        Set<WalkResult> currentTiles = new HashSet<>();

        for (Direction direction : Direction.values()) {
            try {
                WalkResult tmpTile = startingTile.getNext(direction, localTiles);
                if (tmpTile.newTile.getPrevious(tmpTile.direction, localTiles).equals(startingTile)) {
                    currentTiles.add(tmpTile);
                    localTiles.remove(tmpTile.newTile);
                }
            } catch (NoSuchElementException | IllegalStateException ignored) {
            }
        }
        if (currentTiles.size() != 2) {
            throw new IllegalStateException();
        }

        localTiles.remove(startingTile);
        int position = 1;
        do {
            currentTiles = currentTiles.stream()
                    .map(walkResult -> walkResult.newTile.getNext(walkResult.direction, localTiles))
                    .collect(Collectors.toSet());
            currentTiles.forEach(walkResult -> localTiles.remove(walkResult.newTile));
            position++;
        } while (currentTiles.size() == 2);
        return position;
    }

    public static long part2(List<Tile> tiles) {
        return 0;
    }

    public enum PipeDirection {
        UP_DOWN,
        LEFT_RIGHT,
        UP_RIGHT,
        UP_LEFT,
        DOWN_RIGHT,
        DOWN_LEFT,
        START
    }

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;
    }

    public record WalkResult(Tile newTile, Direction direction) {
        @Override
        public int hashCode() {
            return newTile.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj instanceof WalkResult other)
                return newTile.equals(other.newTile);
            return false;
        }
    }

    public static class Tile {
        public final int i;
        public final int j;
        public final PipeDirection pipeDirection;

        public Tile(int i, int j, String pipeDirection) {
            this.i = i;
            this.j = j;
            this.pipeDirection = switch (pipeDirection) {
                case "|" -> PipeDirection.UP_DOWN;
                case "-" -> PipeDirection.LEFT_RIGHT;
                case "L" -> PipeDirection.UP_RIGHT;
                case "J" -> PipeDirection.UP_LEFT;
                case "7" -> PipeDirection.DOWN_LEFT;
                case "F" -> PipeDirection.DOWN_RIGHT;
                case "S" -> PipeDirection.START;
                default -> throw new IllegalStateException();
            };
        }

        public Tile getPrevious(Direction inputDirection, List<Tile> tiles) {
            int wantedI = -1;
            int wantedJ = -1;
            switch (inputDirection) {
                case UP -> {
                    switch (pipeDirection) {
                        case UP_DOWN, DOWN_LEFT, DOWN_RIGHT -> {
                            wantedI = i + 1;
                            wantedJ = j;
                        }
                    }
                }
                case DOWN -> {
                    switch (pipeDirection) {
                        case UP_DOWN, UP_LEFT, UP_RIGHT -> {
                            wantedI = i - 1;
                            wantedJ = j;
                        }
                    }
                }
                case LEFT -> {
                    switch (pipeDirection) {
                        case LEFT_RIGHT, UP_RIGHT, DOWN_RIGHT -> {
                            wantedI = i;
                            wantedJ = j + 1;
                        }
                    }
                }
                case RIGHT -> {
                    switch (pipeDirection) {
                        case LEFT_RIGHT, UP_LEFT, DOWN_LEFT -> {
                            wantedI = i;
                            wantedJ = j - 1;
                        }
                    }
                }
            }
            if (wantedJ == -1)
                throw new IllegalStateException();
            int finalWantedI = wantedI;
            int finalWantedJ = wantedJ;
            return tiles.stream()
                    .filter(tile -> tile.i == finalWantedI)
                    .filter(tile -> tile.j == finalWantedJ)
                    .findFirst()
                    .orElseThrow();
        }

        public WalkResult getNext(Direction inputDirection, List<Tile> tiles) {
            int wantedI = -1;
            int wantedJ = -1;
            Direction direction = null;
            switch (inputDirection) {
                case UP -> {
                    switch (pipeDirection) {
                        case UP_DOWN, START -> {
                            wantedI = i - 1;
                            wantedJ = j;
                            direction = Direction.UP;
                        }
                        case DOWN_LEFT -> {
                            wantedI = i;
                            wantedJ = j - 1;
                            direction = Direction.LEFT;
                        }
                        case DOWN_RIGHT -> {
                            wantedI = i;
                            wantedJ = j + 1;
                            direction = Direction.RIGHT;
                        }
                    }
                }
                case DOWN -> {
                    switch (pipeDirection) {
                        case UP_DOWN, START -> {
                            wantedI = i + 1;
                            wantedJ = j;
                            direction = Direction.DOWN;
                        }
                        case UP_LEFT -> {
                            wantedI = i;
                            wantedJ = j - 1;
                            direction = Direction.LEFT;
                        }
                        case UP_RIGHT -> {
                            wantedI = i;
                            wantedJ = j + 1;
                            direction = Direction.RIGHT;
                        }
                    }
                }
                case LEFT -> {
                    switch (pipeDirection) {
                        case LEFT_RIGHT, START -> {
                            wantedI = i;
                            wantedJ = j - 1;
                            direction = Direction.LEFT;
                        }
                        case UP_RIGHT -> {
                            wantedI = i - 1;
                            wantedJ = j;
                            direction = Direction.UP;
                        }
                        case DOWN_RIGHT -> {
                            wantedI = i + 1;
                            wantedJ = j;
                            direction = Direction.DOWN;
                        }
                    }
                }
                case RIGHT -> {
                    switch (pipeDirection) {
                        case LEFT_RIGHT, START -> {
                            wantedI = i;
                            wantedJ = j + 1;
                            direction = Direction.RIGHT;
                        }
                        case UP_LEFT -> {
                            wantedI = i - 1;
                            wantedJ = j;
                            direction = Direction.UP;
                        }
                        case DOWN_LEFT -> {
                            wantedI = i + 1;
                            wantedJ = j;
                            direction = Direction.DOWN;
                        }
                    }
                }
            }
            if (wantedJ == -1)
                throw new IllegalStateException();
            int finalWantedI = wantedI;
            int finalWantedJ = wantedJ;
            return new WalkResult(tiles.stream()
                    .filter(tile -> tile.i == finalWantedI)
                    .filter(tile -> tile.j == finalWantedJ)
                    .findFirst()
                    .orElseThrow(), direction);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, j);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj instanceof Tile other) {
                return this.i == other.i && this.j == other.j;
            }
            return false;
        }

        @Override
        public String toString() {
            return "[" + i + ", " + j + "] " + pipeDirection;
        }
    }
}
