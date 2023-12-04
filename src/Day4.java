import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day4 {

    public static void main(String[] args) throws IOException {
        List<List<List<Integer>>> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day4-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static List<List<List<Integer>>> parseInput(Stream<String> lines) {
        List<List<List<Integer>>> cards = new ArrayList<>();
        lines.forEach(line -> {
            String[] numbers = line.split(":")[1].trim().split("\\|");
            List<Integer> winningNumbers = new ArrayList<>();
            List<Integer> gottenNumbers = new ArrayList<>();
            for (String number : numbers[0].trim().split(" ")) {
                if (number.isEmpty())
                    continue;
                winningNumbers.add(Integer.parseInt(number.trim()));
            }
            for (String number : numbers[1].trim().split(" ")) {
                if (number.isEmpty())
                    continue;
                gottenNumbers.add(Integer.parseInt(number.trim()));
            }
            cards.add(List.of(winningNumbers, gottenNumbers));
        });
        return cards;
    }

    public static int part1(List<List<List<Integer>>> cards) {
        AtomicInteger sum = new AtomicInteger();
        cards.forEach(numbers -> {
            List<Integer> winningNumbers = numbers.get(1)
                    .stream()
                    .filter(numbers.get(0)::contains)
                    .toList();
            if (!winningNumbers.isEmpty()) {
                sum.addAndGet((int) Math.pow(2, winningNumbers.size() - 1));
            }
        });

        return sum.get();
    }


    public static int part2(List<List<List<Integer>>> cards) {
        return 0;
    }
}
