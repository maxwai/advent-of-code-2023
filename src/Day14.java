import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Day14 {

    public static void main(String[] args) throws IOException {
        List<String> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day14-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static List<String> parseInput(Stream<String> lines) {
        List<String> plattform = new ArrayList<>();
        lines.forEach(plattform::add);
        return plattform;
    }

    public static long part1(List<String> plattform) {
        plattform = new ArrayList<>(plattform);
        boolean changed;
        do {
            changed = false;
            for (int i = 1; i < plattform.size(); i++) {
                for (int j = 0; j < plattform.get(i).length(); j++) {
                    if (plattform.get(i).charAt(j) == 'O' && plattform.get(i - 1).charAt(j) == '.') {
                        plattform.set(i, plattform.get(i).substring(0, j) + '.' + plattform.get(i).substring(j + 1));
                        plattform.set(i - 1, plattform.get(i - 1).substring(0, j) + 'O' + plattform.get(i - 1).substring(j + 1));
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

    public static long part2(List<String> plattform) {
        Map<List<String>, Integer> donePatterns = new HashMap<>();
        donePatterns.put(plattform, 0);

        boolean changed;
        for (int x = 0; x < 1000000000; x++) {
            plattform = new ArrayList<>(plattform);
            do {
                changed = false;
                for (int i = 1; i < plattform.size(); i++) {
                    for (int j = 0; j < plattform.get(i).length(); j++) {
                        if (plattform.get(i).charAt(j) == 'O' && plattform.get(i - 1).charAt(j) == '.') {
                            plattform.set(i, plattform.get(i).substring(0, j) + '.' + plattform.get(i).substring(j + 1));
                            plattform.set(i - 1, plattform.get(i - 1).substring(0, j) + 'O' + plattform.get(i - 1).substring(j + 1));
                            changed = true;
                        }
                    }
                }
            } while (changed);
            do {
                changed = false;
                for (int i = 0; i < plattform.size(); i++) {
                    for (int j = 1; j < plattform.get(i).length(); j++) {
                        if (plattform.get(i).charAt(j) == 'O' && plattform.get(i).charAt(j - 1) == '.') {
                            plattform.set(i, plattform.get(i).substring(0, j - 1) + "O." + plattform.get(i).substring(j + 1));
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
                            plattform.set(i, plattform.get(i).substring(0, j) + '.' + plattform.get(i).substring(j + 1));
                            plattform.set(i + 1, plattform.get(i + 1).substring(0, j) + 'O' + plattform.get(i + 1).substring(j + 1));
                            changed = true;
                        }
                    }
                }
            } while (changed);
            do {
                changed = false;
                for (int i = 0; i < plattform.size(); i++) {
                    for (int j = 0; j < plattform.get(i).length() - 1; j++) {
                        if (plattform.get(i).charAt(j) == 'O' && plattform.get(i).charAt(j + 1) == '.') {
                            plattform.set(i, plattform.get(i).substring(0, j) + ".O" + plattform.get(i).substring(j + 2));
                            changed = true;
                        }
                    }
                }
            } while (changed);
            int index = donePatterns.getOrDefault(plattform, -1);
            if (index != -1) {
                int finalX = ((1000000000 - x) / index) * index + x;
                finalX++;
                while (finalX != 1000000000) {
                    index = (index + 1) % donePatterns.size();
                    int finalIndex = index;
                    plattform = donePatterns.entrySet()
                            .stream()
                            .filter(entry -> entry.getValue() == finalIndex)
                            .map(Map.Entry::getKey)
                            .findAny()
                            .orElseThrow();
                    finalX++;
                }
                break;
            } else {
                donePatterns.put(plattform, x+1);
            }
        }
        long sum = 0;
        for (int i = 0; i < plattform.size(); i++) {
            sum += (plattform.size() - i) * plattform.get(i)
                    .chars()
                    .filter(ch -> (char) ch == 'O')
                    .count();
        }
        return sum;
    }
}
