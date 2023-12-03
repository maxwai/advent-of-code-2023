import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day3 {

    public static void main(String[] args) throws IOException {
        List<List<Character>> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day3-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static List<List<Character>> parseInput(Stream<String> lines) {
        List<List<Character>> gameMap = new ArrayList<>();
        lines.forEach(line -> {
            List<Character> lineList = new ArrayList<>();
            line.chars().forEach(character -> lineList.add((char) character));
            gameMap.add(lineList);
        });
        return gameMap;
    }

    public static int part1(List<List<Character>> lines) {
        int sum = 0;
        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.get(i).size(); j++) {
                if (Character.isDigit(lines.get(i).get(j))) {
                    int z = j;
                    while (z + 1 < lines.get(i).size() && Character.isDigit(lines.get(i).get(z + 1))) {
                        z++;
                    }
                    int partNumber = Integer.parseInt(lines.get(i).stream().skip(j).limit(z - j + 1).map(String::valueOf).reduce("", (character, character2) -> character + character2));
                    outerLoop:
                    for (int x = i == 0 ? i : i - 1; x <= i + 1 && x < lines.size(); x++) {
                        for (int y = j == 0 ? j : j - 1; y <= z + 1 && y < lines.get(x).size(); y++) {
                            if (!Character.isDigit(lines.get(x).get(y)) && lines.get(x).get(y) != '.') {
                                sum += partNumber;
                                break outerLoop;
                            }
                        }
                    }
                    j = z;
                }
            }
        }
        return sum;
    }


    public static int part2(List<List<Character>> lines) {
        return 0;
    }
}
