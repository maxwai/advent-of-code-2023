import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Day1 {

    public static void main(String[] args) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("Day1-input.txt"))) {
            System.out.println(part1(reader.lines()));
        }
        try (BufferedReader reader = new BufferedReader(new FileReader("Day1-input.txt"))) {
            System.out.println(part2(reader.lines()));
        }
    }

    public static int part1(Stream<String> lines) {
        AtomicInteger sum = new AtomicInteger();
        lines.forEach(line -> {
            int firstDigit = line.chars().filter(c -> c >= '0' && c <= '9').findFirst().orElseThrow() - '0';
            int lastDigit = line.chars().filter(c -> c >= '0' && c <= '9').reduce((first, second) -> second).orElseThrow() - '0';
            sum.addAndGet(firstDigit * 10 + lastDigit);
        });
        return sum.get();
    }

    public static int part2(Stream<String> lines) {
        return part1(lines.map(line -> {
            boolean changed;
            do {
                changed = false;
                for (int i = 0; i < line.length(); i++) {
                    if (line.startsWith("one", i)) {
                        line = line.replace("one", "o1e");
                        changed = true;
                        break;
                    }
                    if (line.startsWith("two", i)) {
                        line = line.replace("two", "t2o");
                        changed = true;
                        break;
                    }
                    if (line.startsWith("three", i)) {
                        line = line.replace("three", "t3e");
                        changed = true;
                        break;
                    }
                    if (line.startsWith("four", i)) {
                        line = line.replace("four", "f4r");
                        changed = true;
                        break;
                    }
                    if (line.startsWith("five", i)) {
                        line = line.replace("five", "f5e");
                        changed = true;
                        break;
                    }
                    if (line.startsWith("six", i)) {
                        line = line.replace("six", "s6x");
                        changed = true;
                        break;
                    }
                    if (line.startsWith("seven", i)) {
                        line = line.replace("seven", "s7n");
                        changed = true;
                        break;
                    }
                    if (line.startsWith("eight", i)) {
                        line = line.replace("eight", "e8t");
                        changed = true;
                        break;
                    }
                    if (line.startsWith("nine", i)) {
                        line = line.replace("nine", "n9e");
                        changed = true;
                        break;
                    }

                }
            } while (changed);
            return line;
        }));
    }
}
