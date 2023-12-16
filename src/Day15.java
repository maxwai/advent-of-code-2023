import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class Day15 {

    public static void main(String[] args) throws IOException {
        List<String> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("inputs/Day15-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static List<String> parseInput(Stream<String> lines) {
        List<String> sequences = new ArrayList<>();
        lines.forEach(line -> sequences.addAll(Arrays.asList(line.split(","))));
        return sequences;
    }

    public static long part1(List<String> sequences) {
        return sequences.stream()
                .mapToInt(Day15::hash)
                .sum();
    }

    public static long part2(List<String> sequences) {
        Map<Integer, Box> boxes = new HashMap<>();
        Map<String, Lens> lenses = new HashMap<>();

        sequences.forEach(sequence -> {
            if (sequence.contains("-")) {
                Lens lens = lenses.remove(sequence.substring(0, sequence.length() - 1));
                if (lens != null)
                    lens.removeLens();
            } else {
                String[] split = sequence.split("=");
                if (lenses.containsKey(split[0])) {
                    lenses.get(split[0]).focalLength = Integer.parseInt(split[1]);
                } else {
                    Lens lens = new Lens(split[0], Integer.parseInt(split[1]));
                    int hash = hash(split[0]);
                    if (!boxes.containsKey(hash))
                        boxes.put(hash, new Box(hash));
                    Box box = boxes.get(hash);
                    box.addLens(lens);
                    lenses.put(split[0], lens);
                }
            }
        });


        return lenses.values()
                .stream()
                .mapToInt(lens -> (lens.box.number + 1) * (lens.box.lenses.indexOf(lens) + 1) * lens.focalLength)
                .sum();
    }

    public static int hash(String input) {
        int hash = 0;
        for (char c : input.toCharArray()) {
            hash += c;
            hash *= 17;
            hash %= 256;
        }
        return hash;
    }

    public static class Box {
        public final int number;
        public final List<Lens> lenses = new ArrayList<>();

        public Box(int number) {
            this.number = number;
        }

        public void addLens(Lens lens) {
            lenses.add(lens);
            lens.box = this;
        }
    }

    public static class Lens {
        public final String label;
        public int focalLength;
        public Box box;

        public Lens(String label, int focalLength) {
            this.label = label;
            this.focalLength = focalLength;
        }

        public void removeLens() {
            box.lenses.remove(this);
        }
    }
}
