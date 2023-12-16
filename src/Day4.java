import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Day4 {

    public static void main(String[] args) throws IOException {
        List<List<List<Integer>>> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("inputs/Day4-input.txt"))) {
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
            long winningNumbers = numbers.get(1)
                    .stream()
                    .filter(numbers.get(0)::contains)
                    .count();
            if (winningNumbers != 0) {
                sum.addAndGet((int) Math.pow(2, winningNumbers - 1));
            }
        });

        return sum.get();
    }


    public static int part2(List<List<List<Integer>>> cards) {
        Map<Integer, Integer> cardAmount = new HashMap<>();
        for (int i = 0; i < cards.size(); i++) {
            cardAmount.put(i, 1);
        }
        for (int i = 0; i < cards.size(); i++) {
            long winningNumbers = cards.get(i).get(1)
                    .stream()
                    .filter(cards.get(i).get(0)::contains)
                    .count();
            for (int j = i + 1; j < i + 1 + winningNumbers && j < cards.size(); j++) {
                cardAmount.put(j, cardAmount.get(j) + cardAmount.get(i));
            }
        }
        return cardAmount.values()
                .stream()
                .mapToInt(a -> a)
                .sum();
    }
}
