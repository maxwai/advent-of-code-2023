import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Day9 {

    public static void main(String[] args) throws IOException {
        List<List<Integer>> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day9-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input)); // 1898776583
        System.out.println(part2(input)); // 1100
    }

    public static List<List<Integer>> parseInput(Stream<String> lines) {
        List<List<Integer>> sequences = new ArrayList<>();
        lines.forEach(line -> {
            List<Integer> currentSequence = new ArrayList<>();
            String[] numbers = line.split(" ");
            Arrays.stream(numbers)
                    .map(Integer::parseInt)
                    .forEach(currentSequence::add);
            sequences.add(currentSequence);
        });
        return sequences;
    }

    public static long part1(List<List<Integer>> sequences) {
        return sequences.stream()
                .mapToLong(sequence -> getLast(sequence, true))
                .sum();
    }

    public static long part2(List<List<Integer>> sequences) {
        return sequences.stream()
                .mapToLong(sequence -> getLast(sequence, false))
                .sum();
    }

    public static long getLast(List<Integer> sequence, boolean left) {
        if (sequence.isEmpty())
            throw new IllegalStateException();

        if (sequence.size() == 1)
            return 0;

        List<Integer> newSequence = new ArrayList<>();
        for (int i = 0; i < sequence.size() - 1; i++) {
            newSequence.add(sequence.get(i + 1) - sequence.get(i));
        }
        if (left)
            return sequence.get(sequence.size() - 1) + getLast(newSequence, true);
        else
            return sequence.get(0) - getLast(newSequence, false);
    }
}
