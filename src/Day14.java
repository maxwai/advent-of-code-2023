import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day14 {

    public static void main(String[] args) throws IOException {
        List<StringBuilder> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day14-input.txt"))) {
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
        return 0;
    }
}
