import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Day1 {

    public static void main(String[] args) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("Day1-input.txt"))) {
            AtomicInteger sum = new AtomicInteger();
            reader.lines().forEach(line -> {
                int firstDigit = line.chars().filter(c -> c >= '0' && c <= '9').findFirst().getAsInt() - '0';
                int lastDigit = line.chars().filter(c -> c >= '0' && c <= '9').reduce((first, second) -> second).getAsInt() - '0';
                sum.addAndGet(firstDigit * 10 + lastDigit);
            });
            System.out.println(sum.get());
        }
    }
}
