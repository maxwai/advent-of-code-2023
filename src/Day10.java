import java.io.*;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day10 {

    public static void main(String[] args) throws IOException {
        List<Tile> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("inputs/Day10-input.txt"))) {
            input = parseInput(reader.lines());
        }
        // 6738 & 579
        Part1Result part1 = part1(input);
        System.out.println(part1.answer);
        System.out.println(part2(part1.loop));
    }

    public static List<Tile> parseInput(Stream<String> lines) {
        List<List<String>> pipes = new ArrayList<>();
        lines.forEach(line -> pipes.add(new ArrayList<>(Arrays.asList(line.split("")))));

        List<Tile> tiles = new ArrayList<>();
        Map<Coords, Tile> tileMap = new HashMap<>();
        for (int i = 0; i < pipes.size(); i++) {
            for (int j = 0; j < pipes.get(i).size(); j++) {
                if (pipes.get(i).get(j).equals(".")) {
                    continue;
                }
                Tile tile = new Tile(i, j, pipes.get(i).get(j));
                tiles.add(tile);
                tileMap.put(new Coords(i, j), tile);
            }
        }

        tiles.forEach(tile -> tile.initializeConnections(tileMap));
        return tiles;
    }

    public static Part1Result part1(List<Tile> tiles) {
        Set<Tile> loopTiles = new HashSet<>();
        Tile startingTile = tiles.stream()
                .filter(tile -> tile.pipeDirection == PipeDirection.START)
                .findAny()
                .orElseThrow();
        loopTiles.add(startingTile);

        WalkResult currentTile = null;
        for (Direction direction : Direction.values()) {
            try {
                WalkResult tmpTile = startingTile.getNext(direction);
                currentTile = tmpTile;
                loopTiles.add(tmpTile.newTile);
                tiles.remove(tmpTile.newTile);
                break;
            } catch (NoSuchElementException ignored) {
            }
        }
        if (currentTile == null) {
            throw new IllegalStateException();
        }
        tiles.remove(startingTile);

        int position = 1;
        do {
            currentTile = currentTile.newTile.getNext(currentTile.direction);
            loopTiles.add(currentTile.newTile);
            position++;
        } while (!currentTile.newTile.equals(startingTile));
        return new Part1Result(position / 2, loopTiles);
    }

    public static long part2(Set<Tile> loopTiles) {
        int pixelAmount = 140 * 3;

        List<StringBuilder> output = new ArrayList<>();
        IntStream.range(0, pixelAmount).forEach(x -> output.add(new StringBuilder(".".repeat(pixelAmount))));

        loopTiles.forEach(tile -> {
            output.get(3 * tile.i).setCharAt(3 * tile.j, tile.pipeDirection.middleSign);
            if (tile.pipeDirection.up) output.get(3 * tile.i - 1).setCharAt(3 * tile.j, '|');
            if (tile.pipeDirection.down) output.get(3 * tile.i + 1).setCharAt(3 * tile.j, '|');
            if (tile.pipeDirection.left) output.get(3 * tile.i).setCharAt(3 * tile.j - 1, '-');
            if (tile.pipeDirection.right) output.get(3 * tile.i).setCharAt(3 * tile.j + 1, '-');
        });

        IntStream.range(0, pixelAmount)
                .forEach(i -> {
                    if (output.get(0).charAt(i) == '.')
                        output.get(0).setCharAt(i, ' ');
                    if (output.get(pixelAmount - 1).charAt(i) == '.')
                        output.get(pixelAmount - 1).setCharAt(i, ' ');
                    if (output.get(i).charAt(0) == '.')
                        output.get(i).setCharAt(0, ' ');
                    if (output.get(i).charAt(pixelAmount - 1) == '.')
                        output.get(i).setCharAt(pixelAmount - 1, ' ');
                });

        Queue<Coords> toCheck = new LinkedList<>();
        for (int i = 0; i < pixelAmount; i++) {
            for (int j = 0; j < pixelAmount; j++) {
                if (output.get(i).charAt(j) == ' ')
                    toCheck.add(new Coords(i, j));
            }
        }
        while (!toCheck.isEmpty()) {
            Coords coords = toCheck.remove();
            for (int x = -1; x <= 1; x += 2) {
                if (coords.i + x >= 0 && coords.i + x < pixelAmount && output.get(coords.i + x).charAt(coords.j) == '.') {
                    output.get(coords.i + x).setCharAt(coords.j, ' ');
                    toCheck.add(new Coords(coords.i + x, coords.j));
                }
                if (coords.j + x >= 0 && coords.j + x < pixelAmount && output.get(coords.i).charAt(coords.j + x) == '.') {
                    output.get(coords.i).setCharAt(coords.j + x, ' ');
                    toCheck.add(new Coords(coords.i, coords.j + x));
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Day10-input-big.txt"))) {
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

        List<StringBuilder> smallOutput = new ArrayList<>();
        IntStream.range(0, 140).forEach(x -> smallOutput.add(new StringBuilder(140)));

        for (int i = 0; i < output.size(); i += 3) {
            for (int j = 0; j < output.get(i).length(); j += 3) {
                smallOutput.get(i / 3).append(output.get(i).charAt(j));
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Day10-input-small.txt"))) {
            smallOutput.forEach(stringBuilder -> {
                try {
                    writer.write(stringBuilder + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return smallOutput.stream()
                .mapToLong(line -> line.chars()
                        .filter(ch -> ch == '.')
                        .count())
                .sum();
    }

    public enum PipeDirection {
        UP_DOWN('|', true, true, false, false),
        LEFT_RIGHT('-', false, false, true, true),
        UP_RIGHT('└', true, false, false, true),
        UP_LEFT('┘', true, false, true, false),
        DOWN_RIGHT('┌', false, true, false, true),
        DOWN_LEFT('┐', false, true, true, false),
        START('S', true, true, true, true);

        public final boolean up, down, left, right;
        public final char middleSign;

        PipeDirection(char middleSign, boolean up, boolean down, boolean left, boolean right) {
            this.middleSign = middleSign;
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
        }

        public static PipeDirection create(String code) {
            return switch (code) {
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
    }

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;

        public Direction reverse() {
            return this == UP ? DOWN : (this == DOWN ? UP : (this == LEFT ? RIGHT : LEFT));
        }
    }

    public record Coords(int i, int j) {
    }

    public record Part1Result(long answer, Set<Tile> loop) {
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
        private final Map<Direction, Tile> connections = new HashMap<>();

        public Tile(int i, int j, String pipeDirection) {
            this.i = i;
            this.j = j;
            this.pipeDirection = PipeDirection.create(pipeDirection);
        }

        public void initializeConnections(Map<Coords, Tile> tiles) {
            switch (pipeDirection) {
                case UP_DOWN, UP_LEFT, UP_RIGHT -> Optional.ofNullable(tiles.get(new Coords(i - 1, j)))
                        .ifPresent(tile -> connections.put(Direction.UP, tile));
            }
            switch (pipeDirection) {
                case UP_DOWN, DOWN_LEFT, DOWN_RIGHT -> Optional.ofNullable(tiles.get(new Coords(i + 1, j)))
                        .ifPresent(tile -> connections.put(Direction.DOWN, tile));
            }
            switch (pipeDirection) {
                case LEFT_RIGHT, UP_LEFT, DOWN_LEFT -> Optional.ofNullable(tiles.get(new Coords(i, j - 1)))
                        .ifPresent(tile -> connections.put(Direction.LEFT, tile));
            }
            switch (pipeDirection) {
                case LEFT_RIGHT, UP_RIGHT, DOWN_RIGHT -> Optional.ofNullable(tiles.get(new Coords(i, j + 1)))
                        .ifPresent(tile -> connections.put(Direction.RIGHT, tile));
            }
            if (pipeDirection == PipeDirection.START) {
                Optional.ofNullable(tiles.get(new Coords(i - 1, j)))
                        .filter(tile -> tile.pipeDirection == PipeDirection.UP_DOWN ||
                                        tile.pipeDirection == PipeDirection.DOWN_LEFT ||
                                        tile.pipeDirection == PipeDirection.DOWN_RIGHT)
                        .ifPresent(tile -> connections.put(Direction.UP, tile));
                Optional.ofNullable(tiles.get(new Coords(i + 1, j)))
                        .filter(tile -> tile.pipeDirection == PipeDirection.UP_DOWN ||
                                        tile.pipeDirection == PipeDirection.UP_LEFT ||
                                        tile.pipeDirection == PipeDirection.UP_RIGHT)
                        .ifPresent(tile -> connections.put(Direction.DOWN, tile));
                Optional.ofNullable(tiles.get(new Coords(i, j - 1)))
                        .filter(tile -> tile.pipeDirection == PipeDirection.LEFT_RIGHT ||
                                        tile.pipeDirection == PipeDirection.DOWN_RIGHT ||
                                        tile.pipeDirection == PipeDirection.UP_RIGHT)
                        .ifPresent(tile -> connections.put(Direction.LEFT, tile));
                Optional.ofNullable(tiles.get(new Coords(i, j + 1)))
                        .filter(tile -> tile.pipeDirection == PipeDirection.LEFT_RIGHT ||
                                        tile.pipeDirection == PipeDirection.DOWN_LEFT ||
                                        tile.pipeDirection == PipeDirection.UP_LEFT)
                        .ifPresent(tile -> connections.put(Direction.RIGHT, tile));
            }
        }

        public WalkResult getNext(Direction inputDirection) {
            return connections.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey() != inputDirection)
                    .findAny()
                    .map(entry -> new WalkResult(entry.getValue(), entry.getKey().reverse()))
                    .orElseThrow();
        }

        @Override
        public int hashCode() {
            return i + j << 16;
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
