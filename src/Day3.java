import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day3 {

    public static void main(String[] args) throws IOException {
        List<List<Character>> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("inputs/Day3-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input)); // 536576
        System.out.println(part2(input)); // 75741499
    }

    public static List<List<Character>> parseInput(Stream<String> lines) {
        List<List<Character>> gameMap = new ArrayList<>();
        lines.forEach(line -> {
            List<Character> lineList = new ArrayList<>();
            lineList.add('.');
            line.chars().forEach(character -> lineList.add((char) character));
            lineList.add('.');
            gameMap.add(lineList);
        });
        gameMap.add(0, new ArrayList<>(Collections.nCopies(gameMap.get(0).size(), '.')));
        gameMap.add(new ArrayList<>(Collections.nCopies(gameMap.get(gameMap.size() - 1).size(), '.')));
        return gameMap;
    }

    public static int part1(List<List<Character>> lines) {
        int sum = 0;
        for (int i = 1; i < lines.size() - 1; i++) {
            for (int j = 1; j < lines.get(i).size() - 1; j++) {
                if (Character.isDigit(lines.get(i).get(j))) {
                    int partNumberLength = 0;
                    for (int z = j; Character.isDigit(lines.get(i).get(z)); z++) {
                        partNumberLength++;
                    }
                    int partNumber = Integer.parseInt(lines.get(i)
                            .stream()
                            .skip(j)
                            .limit(partNumberLength)
                            .map(Objects::toString)
                            .collect(Collectors.joining()));
                    int finalJ = j;
                    int finalPartNumberLength = partNumberLength;
                    if (lines.stream()
                            .skip(i - 1)
                            .limit(3)
                            .flatMap(line -> line.stream()
                                    .skip(finalJ - 1)
                                    .limit(finalPartNumberLength + 2))
                            .filter(ch -> !Character.isDigit(ch))
                            .anyMatch(ch -> ch != '.')) {
                        sum += partNumber;
                    }
                    j += partNumberLength;
                }
            }
        }
        return sum;
    }

    public static int part2(List<List<Character>> lines) {
        int sum = 0;
        for (int i = 1; i < lines.size() - 1; i++) {
            for (int j = 1; j < lines.get(i).size() - 1; j++) {
                if (lines.get(i).get(j) == '*') {
                    int gearRatio = 1;
                    int amount = 0;
                    for (int x = i - 1; x <= i + 1; x++) {
                        for (int y = j - 1; y <= j + 1; y++) {
                            if (Character.isDigit(lines.get(x).get(y))) {
                                amount++;
                                int start;
                                //noinspection StatementWithEmptyBody
                                for (start = y; Character.isDigit(lines.get(x).get(start - 1)); start--) ;
                                int partNumberLength = 0;
                                for (int z = start; Character.isDigit(lines.get(x).get(z)); z++) {
                                    partNumberLength++;
                                }
                                int partNumber = Integer.parseInt(lines.get(x)
                                        .stream()
                                        .skip(start)
                                        .limit(partNumberLength)
                                        .map(Objects::toString)
                                        .collect(Collectors.joining()));
                                gearRatio *= partNumber;
                                y += partNumberLength - y + start;
                            }
                        }
                    }
                    if (amount == 2) {
                        sum += gearRatio;
                    }
                }
            }
        }
        return sum;
    }
}
