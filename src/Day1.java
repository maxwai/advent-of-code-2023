import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Day1 {

    public static void main(String[] args) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("Day1-input.txt"))) {
            AtomicInteger sum = new AtomicInteger();
            reader.lines().forEach(line -> {
                System.out.print(line + " -> ");
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
                System.out.println(line);
                int firstDigit = line.chars().filter(c -> c >= '0' && c <= '9').findFirst().getAsInt() - '0';
                int lastDigit = line.chars().filter(c -> c >= '0' && c <= '9').reduce((first, second) -> second).getAsInt() - '0';
                sum.addAndGet(firstDigit * 10 + lastDigit);
            });
            System.out.println(sum.get());
        }
    }
}
