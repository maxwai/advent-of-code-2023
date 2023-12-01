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
        return part1(lines.map(line -> line.replaceAll("one", "o1e")
                .replaceAll("two", "t2o")
                .replaceAll("three", "t3e")
                .replaceAll("four", "f4r")
                .replaceAll("five", "f5e")
                .replaceAll("six", "s6x")
                .replaceAll("seven", "s7n")
                .replaceAll("eight", "e8t")
                .replaceAll("nine", "n9e")));
    }
}
