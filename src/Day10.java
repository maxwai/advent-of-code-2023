import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

        Tile startingTile = tiles.stream()
                .filter(tile -> tile.pipeDirection == PipeDirection.START)
                .findAny()
                .orElseThrow();

        Set<WalkResult> currentTiles = new HashSet<>();

        for (Direction direction : Direction.values()) {
            try {
                WalkResult tmpTile = startingTile.getNext(direction, tiles);
                if (tmpTile.newTile.getPrevious(tmpTile.direction, tiles).equals(startingTile)) {
                    currentTiles.add(tmpTile);
                }
            } catch (NoSuchElementException | IllegalStateException ignored) {
            }
        }
        if (currentTiles.size() != 2) {
            throw new IllegalStateException();
        }

        int position = 1;
        do {
            currentTiles = currentTiles.stream()
                    .map(walkResult -> walkResult.newTile.getNext(walkResult.direction, tiles))
                    .collect(Collectors.toSet());
            position++;
        } while (currentTiles.size() == 2);
        return position;
    }

    public static long part2(List<Tile> tiles) {
        Set<Tile> loopTiles = new HashSet<>();

        Tile startingTile = tiles.stream()
                .filter(tile -> tile.pipeDirection == PipeDirection.START)
                .findAny()
                .orElseThrow();
        loopTiles.add(startingTile);

        Set<WalkResult> currentTiles = new HashSet<>();

        for (Direction direction : Direction.values()) {
            try {
                WalkResult tmpTile = startingTile.getNext(direction, tiles);
                if (tmpTile.newTile.getPrevious(tmpTile.direction, tiles).equals(startingTile)) {
                    currentTiles.add(tmpTile);
                    loopTiles.add(tmpTile.newTile);
                }
            } catch (NoSuchElementException | IllegalStateException ignored) {
            }
        }
        if (currentTiles.size() != 2) {
            throw new IllegalStateException();
        }

        do {
            currentTiles = currentTiles.stream()
                    .map(walkResult -> walkResult.newTile.getNext(walkResult.direction, tiles))
                    .peek(walkResult -> loopTiles.add(walkResult.newTile))
                    .collect(Collectors.toSet());
        } while (currentTiles.size() == 2);

        int pixelAmount = 140 * 3;

        List<StringBuilder> output = new ArrayList<>();
        IntStream.range(0, pixelAmount).forEach(x -> output.add(new StringBuilder(".".repeat(pixelAmount))));

        loopTiles.forEach(tile -> {
            switch (tile.pipeDirection) {
                case UP_DOWN -> {
                    output.get(3 * tile.i - 1).setCharAt(3 * tile.j, '|');
                    output.get(3 * tile.i).setCharAt(3 * tile.j, '|');
                    output.get(3 * tile.i + 1).setCharAt(3 * tile.j, '|');
                }
                case LEFT_RIGHT -> output.get(3 * tile.i).replace(3 * tile.j - 1, 3 * tile.j + 2, "---");
                case UP_RIGHT -> {
                    output.get(3 * tile.i - 1).setCharAt(3 * tile.j, '|');
                    output.get(3 * tile.i).replace(3 * tile.j, 3 * tile.j + 2, "└-");
                }
                case UP_LEFT -> {
                    output.get(3 * tile.i - 1).setCharAt(3 * tile.j, '|');
                    output.get(3 * tile.i).replace(3 * tile.j - 1, 3 * tile.j + 1, "-┘");
                }
                case DOWN_RIGHT -> {
                    output.get(3 * tile.i).replace(3 * tile.j, 3 * tile.j + 2, "┌-");
                    output.get(3 * tile.i + 1).setCharAt(3 * tile.j, '|');
                }
                case DOWN_LEFT -> {
                    output.get(3 * tile.i).replace(3 * tile.j - 1, 3 * tile.j + 1, "-┐");
                    output.get(3 * tile.i + 1).setCharAt(3 * tile.j, '|');
                }
                case START -> {
                    output.get(3 * tile.i - 1).setCharAt(3 * tile.j, '|');
                    output.get(3 * tile.i).setCharAt(3 * tile.j, 'S');
                    output.get(3 * tile.i + 1).setCharAt(3 * tile.j, '|');
                }
            }
        });

        for (int i = 0; i < pixelAmount; i++) {
            if (output.get(0).charAt(i) == '.')
                output.get(0).setCharAt(i, ' ');
            if (output.get(pixelAmount - 1).charAt(i) == '.')
                output.get(pixelAmount - 1).setCharAt(i, ' ');
            if (output.get(i).charAt(0) == '.')
                output.get(i).setCharAt(0, ' ');
            if (output.get(i).charAt(pixelAmount - 1) == '.')
                output.get(i).setCharAt(pixelAmount - 1, ' ');
        }

        boolean changed;
        do {
            changed = false;
            for (int i = 1; i < pixelAmount - 1; i++) {
                for (int j = 1; j < pixelAmount - 1; j++) {
                    if (output.get(i).charAt(j) == '.' && (
                            output.get(i - 1).charAt(j) == ' ' ||
                            output.get(i + 1).charAt(j) == ' ' ||
                            output.get(i).charAt(j - 1) == ' ' ||
                            output.get(i).charAt(j + 1) == ' ')) {
                        output.get(i).setCharAt(j, ' ');
                        changed = true;
                    }
                }
            }
        } while (changed);

        for (int i = 0; i < pixelAmount ; i++) {
            for (int j = 0; j < pixelAmount; j++) {
                if (output.get(i).charAt(j) == '.' && (
                        output.get(i - 1).charAt(j) == '-' ||
                        output.get(i + 1).charAt(j) == '-' ||
                        output.get(i - 1).charAt(j) == '└' ||
                        output.get(i).charAt(j + 1) == '└' ||
                        output.get(i - 1).charAt(j + 1) == '└' ||
                        output.get(i - 1).charAt(j) == '┘' ||
                        output.get(i).charAt(j - 1) == '┘' ||
                        output.get(i - 1).charAt(j - 1) == '┘' ||
                        output.get(i + 1).charAt(j) == '┌' ||
                        output.get(i).charAt(j + 1) == '┌' ||
                        output.get(i + 1).charAt(j + 1) == '┌' ||
                        output.get(i + 1).charAt(j) == '┐' ||
                        output.get(i).charAt(j - 1) == '┐' ||
                        output.get(i + 1).charAt(j - 1) == '┐' ||
                        output.get(i).charAt(j + 1) == '|' ||
                        output.get(i).charAt(j - 1) == '|' ||
                        output.get(i).charAt(j + 1) == 'S' ||
                        output.get(i).charAt(j - 1) == 'S')) {
                    output.get(i).setCharAt(j, ' ');
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Day10-input-pixels.txt"))) {
            output.forEach(stringBuilder -> {
                try {
                    writer.write(stringBuilder + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return output.stream()
                .flatMap(stringBuilder -> stringBuilder.chars().mapToObj(ch -> (char) ch))
                .filter(ch -> ch == '.')
                .count() / 9;
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
        RIGHT
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
