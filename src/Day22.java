import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day22 {

    public static void main(String[] args) throws IOException {
        List<Block> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("inputs/Day22-input.txt"))) {
            input = parseInput(reader.lines());
        }
        List<Integer> prework = IntStream.range(0, input.size())
                .mapToObj(i -> drop(copy(input), i))
                .toList();

        System.out.println(part1(prework));
        System.out.println(part2(prework));
    }

    public static List<Block> parseInput(Stream<String> lines) {
        List<Block> blocks = new ArrayList<>();
        lines.forEach(line -> {
            String[] parts = line.split("~");
            String[] start = parts[0].split(",");
            String[] stop = parts[1].split(",");
            blocks.add(new Block(new Coords(Integer.parseInt(start[0]), Integer.parseInt(start[1]), Integer.parseInt(start[2])),
                    new Coords(Integer.parseInt(stop[0]), Integer.parseInt(stop[1]), Integer.parseInt(stop[2]))));
        });
        blocks.sort(null);
        drop(blocks, -1);
        return blocks;
    }

    public static long part1(List<Integer> prework) {
        return prework.stream()
                .filter(falls -> falls == 0)
                .count();
    }

    public static long part2(List<Integer> prework) {
        return prework.stream()
                .mapToInt(i -> i)
                .sum();
    }

    public static List<Block> copy(List<Block> og) {
        return new ArrayList<>(og.stream()
                .map(block -> new Block(new Coords(block.start.x, block.start.y, block.start.z),
                        new Coords(block.stop.x, block.stop.y, block.stop.z)))
                .toList());
    }

    public static int drop(List<Block> blocks, int skip) {
        Map<Coords, Integer> peaks = new HashMap<>();
        int falls = 0;
        for (int i = 0; i < blocks.size(); i++) {
            if (i == skip) {
                continue;
            }
            Block block = blocks.get(i);
            List<Coords> coords = new ArrayList<>();
            for (int a = block.start.x; a <= block.stop.x; a++) {
                for (int b = block.start.y; b <= block.stop.y; b++) {
                    coords.add(new Coords(a, b, 0));
                }
            }
            int peak = coords.stream()
                               .mapToInt(coords1 -> peaks.getOrDefault(coords1, 0))
                               .max()
                               .orElseThrow() + 1;
            int newZ = peak + block.stop.z - block.start.z;
            coords.forEach(coords1 -> peaks.put(coords1, newZ));

            blocks.set(i, new Block(new Coords(block.start.x, block.start.y, peak),
                    new Coords(block.stop.x, block.stop.y, newZ)));
            falls += peak < block.start.z ? 1 : 0;
        }
        return falls;
    }

    public record Coords(int x, int y, int z) {
    }

    public record Block(Coords start, Coords stop) implements Comparable<Block> {
        @Override
        public int compareTo(Block block) {
            return Comparator.comparingInt(b -> ((Block) b).start.z).compare(this, block);
        }
    }
}
