import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class Day14 {

    public static void main(String[] args) throws IOException {
        List<StringBuilder> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("inputs/Day14-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static List<StringBuilder> parseInput(Stream<String> lines) {
        List<StringBuilder> plattform = new ArrayList<>();
        lines.forEach(line -> plattform.add(new StringBuilder(line)));
        return plattform;
    }

    public static long part1(List<StringBuilder> plattform) {
        plattform = new ArrayList<>(plattform);
        boolean changed;
        do {
            changed = false;
            for (int i = 1; i < plattform.size(); i++) {
                for (int j = 0; j < plattform.get(i).length(); j++) {
                    if (plattform.get(i).charAt(j) == 'O' && plattform.get(i - 1).charAt(j) == '.') {
                        plattform.get(i).setCharAt(j, '.');
                        plattform.get(i - 1).setCharAt(j, 'O');
                        changed = true;
                    }
                }
            }
        } while (changed);

        long sum = 0;
        for (int i = 0; i < plattform.size(); i++) {
            sum += (plattform.size() - i) * plattform.get(i)
                    .chars()
                    .filter(ch -> (char) ch == 'O')
                    .count();
        }
        return sum;
    }

    public static long part2(List<StringBuilder> plattform) {
        Map<List<String>, Integer> donePatterns = new HashMap<>();
        donePatterns.put(plattform.stream()
                        .map(StringBuilder::toString)
                        .toList(), 0);

        boolean changed;
        List<String> result = null;
        for (int x = 0; x < 1000000000; x++) {
            plattform = new ArrayList<>(plattform);
            do {
                changed = false;
                for (int i = 1; i < plattform.size(); i++) {
                    for (int j = 0; j < plattform.get(i).length(); j++) {
                        if (plattform.get(i).charAt(j) == 'O' && plattform.get(i - 1).charAt(j) == '.') {
                            plattform.get(i).setCharAt(j, '.');
                            plattform.get(i - 1).setCharAt(j, 'O');
                            changed = true;
                        }
                    }
                }
            } while (changed);
            do {
                changed = false;
                for (StringBuilder stringBuilder : plattform) {
                    for (int j = 1; j < stringBuilder.length(); j++) {
                        if (stringBuilder.charAt(j) == 'O' && stringBuilder.charAt(j - 1) == '.') {
                            stringBuilder.setCharAt(j, '.');
                            stringBuilder.setCharAt(j - 1, 'O');
                            changed = true;
                        }
                    }
                }
            } while (changed);
            do {
                changed = false;
                for (int i = 0; i < plattform.size() - 1; i++) {
                    for (int j = 0; j < plattform.get(i).length(); j++) {
                        if (plattform.get(i).charAt(j) == 'O' && plattform.get(i + 1).charAt(j) == '.') {
                            plattform.get(i).setCharAt(j, '.');
                            plattform.get(i + 1).setCharAt(j, 'O');
                            changed = true;
                        }
                    }
                }
            } while (changed);
            do {
                changed = false;
                for (StringBuilder stringBuilder : plattform) {
                    for (int j = 0; j < stringBuilder.length() - 1; j++) {
                        if (stringBuilder.charAt(j) == 'O' && stringBuilder.charAt(j + 1) == '.') {
                            stringBuilder.setCharAt(j, '.');
                            stringBuilder.setCharAt(j + 1, 'O');
                            changed = true;
                        }
                    }
                }
            } while (changed);
            int index = donePatterns.getOrDefault(plattform.stream()
                        .map(StringBuilder::toString)
                        .toList(), -1);
            if (index != -1) {
                int finalX = ((1000000000 - x) / index) * index + x;
                finalX++;
                while (finalX != 1000000000) {
                    index = (index + 1) % donePatterns.size();
                    int finalIndex = index;
                    result = donePatterns.entrySet()
                            .stream()
                            .filter(entry -> entry.getValue() == finalIndex)
                            .map(Map.Entry::getKey)
                            .findAny()
                            .orElseThrow();
                    finalX++;
                }
                break;
            } else {
                donePatterns.put(plattform.stream()
                        .map(StringBuilder::toString)
                        .toList(), x+1);
            }
        }
        long sum = 0;
        for (int i = 0; i < Objects.requireNonNull(result).size(); i++) {
            sum += (result.size() - i) * result.get(i)
                    .chars()
                    .filter(ch -> (char) ch == 'O')
                    .count();
        }
        return sum;
    }
}
