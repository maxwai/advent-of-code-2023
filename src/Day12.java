import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day12 {

    public static Map<CallArguments, Long> doneCalls = new HashMap<>();

    public static void main(String[] args) throws IOException {
        List<Line> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day12-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static List<Line> parseInput(Stream<String> lines) {
        List<Line> onsens = new ArrayList<>();
        lines.forEach(line -> {
            String[] input = line.split(" ");
            onsens.add(new Line(input[0], Arrays.stream(input[1].split(","))
                    .map(Integer::parseInt)
                    .toList()));
        });

        return onsens;
    }

    public static long part1(List<Line> onsens) {
        return onsens.stream()
                .mapToLong(line -> getEveryPossibility(line.line, line.numbers, 0))
                .sum();
    }

    public static long part2(List<Line> onsens) {
        return onsens.stream()
                .map(line -> {
                    List<Integer> newNumbers = new ArrayList<>();
                    StringBuilder newLine = new StringBuilder();
                    for (int i = 0; i < 5; i++) {
                        newNumbers.addAll(line.numbers);
                        newLine.append(line.line).append("?");
                    }
                    return new Line(newLine.deleteCharAt(newLine.length() - 1).toString(), newNumbers);
                })
                .mapToLong(line -> getEveryPossibility(line.line, line.numbers, 0))
                .sum();
    }

    public static long getEveryPossibility(String line, List<Integer> numbers, int currentPatterSize) {
        CallArguments callArguments = new CallArguments(line, numbers, currentPatterSize);

        long cachedCall = doneCalls.getOrDefault(callArguments, -1L);
        if (cachedCall >= 0)
            return cachedCall;

        if (line.isEmpty()) {
            if ((numbers.size() == 1 && numbers.get(0) == currentPatterSize) ||
                (numbers.isEmpty() && currentPatterSize == 0)) {
                doneCalls.put(callArguments, 1L);
                return 1;
            }
            doneCalls.put(callArguments, 0L);
            return 0;
        }
        char currentChar = line.charAt(0);
        line = line.substring(1);
        int currentNumber = numbers.isEmpty() ? 0 : numbers.get(0);
        switch (currentChar) {
            case '?' -> {
                long result = getEveryPossibility('#' + line, numbers, currentPatterSize) +
                              getEveryPossibility('.' + line, numbers, currentPatterSize);
                doneCalls.put(callArguments, result);
                return result;
            }
            case '#' -> {
                long result = currentPatterSize > currentNumber ? 0 : getEveryPossibility(line, numbers, currentPatterSize + 1);
                doneCalls.put(callArguments, result);
                return result;
            }
            case '.' -> {
                long result = 0;
                if (currentPatterSize == 0) {
                    result = getEveryPossibility(line, numbers, 0);
                } else if (currentPatterSize == currentNumber) {
                    result = getEveryPossibility(line, numbers.subList(1, numbers.size()), 0);
                }
                doneCalls.put(callArguments, result);
                return result;
            }
        }
        throw new IllegalStateException();
    }

    public static long getEveryPossibility(String line, Pattern patternWildcard, Pattern pattern) {
        Queue<String> queue = new LinkedList<>();
        queue.add(line);
        long sum = 0;
        int max_queue_size = 0;
        while (!queue.isEmpty()) {
            max_queue_size = Math.max(queue.size(), max_queue_size);
            line = queue.remove();
            if (!line.contains("?") || pattern.matcher(line).find()) {
                sum += patternWildcard.matcher(line).find() ? 1 : 0;
            } else {
                String tmpString = line.replaceFirst("\\?", "#");
                if (patternWildcard.matcher(tmpString).find())
                    queue.add(tmpString);
                tmpString = line.replaceFirst("\\?", ".");
                if (patternWildcard.matcher(tmpString).find())
                    queue.add(tmpString);
            }
        }
        System.out.println(max_queue_size);
        return sum;
    }

    public static Pattern getPatternWildcard(List<Integer> numbers) {
        StringBuilder pattern = new StringBuilder("^[\\.?]*");
        numbers.forEach(number -> pattern.append("[#?]{").append(number).append("}[\\.?]+"));
        pattern.deleteCharAt(pattern.length() - 1).append("*$");
        return Pattern.compile(pattern.toString());
    }

    public static Pattern getPattern(List<Integer> numbers) {
        StringBuilder pattern = new StringBuilder("^(([\\.?]*");
        numbers.forEach(number -> pattern.append("#{").append(number).append("}[\\.?]+"));
        pattern.deleteCharAt(pattern.length() - 1).append("*)|(\\.*");
        numbers.forEach(number -> pattern.append("[#?]{").append(number).append("}\\.+"));
        pattern.deleteCharAt(pattern.length() - 1).append("*))$");
        return Pattern.compile(pattern.toString());
    }

    public record CallArguments(String line, List<Integer> numbers, int currentPatterSize) {
    }

    public record Line(String line, List<Integer> numbers) {
    }
}
