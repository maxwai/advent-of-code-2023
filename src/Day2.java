import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Day2 {

    public static void main(String[] args) throws IOException {
        Map<Integer, List<Map<String, Integer>>> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day2-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static Map<Integer, List<Map<String, Integer>>> parseInput(Stream<String> lines) {
        Map<Integer, List<Map<String, Integer>>> gameMap = new HashMap<>();
        lines.forEach(line -> {
            String[] split = line.split(":");
            Integer gameId = Integer.parseInt(split[0].split(" ")[1].trim());
            String[] pulls = split[1].trim().split(";");
            List<Map<String, Integer>> pullList = new ArrayList<>();
            for (String pull : pulls) {
                String[] balls = pull.split(",");
                Map<String, Integer> ballMap = new HashMap<>();
                for (String ball : balls) {
                    ball = ball.trim();
                    String[] type = ball.split(" ");
                    ballMap.put(type[1].trim(), Integer.parseInt(type[0].trim()));
                }
                pullList.add(ballMap);
            }
            gameMap.put(gameId, pullList);
        });
        return gameMap;
    }

    public static int part1(Map<Integer, List<Map<String, Integer>>> games) {
        Map<String, Integer> maxBalls = Map.of(
                "red", 12,
                "green", 13,
                "blue", 14);

        return games.entrySet().
                stream()
                .mapToInt(gameEntry -> gameEntry.getValue()
                        .stream()
                        .map(balls -> balls.entrySet()
                                .stream()
                                .map(ballPull -> ballPull.getValue() <= maxBalls.get(ballPull.getKey()))
                                .reduce(true, Boolean::logicalAnd))
                        .reduce(true, Boolean::logicalAnd) ? gameEntry.getKey() : 0)
                .sum();
    }


    public static int part2(Map<Integer, List<Map<String, Integer>>> games) {
        return games.values()
                .stream()
                .mapToInt(pulls -> pulls.stream()
                        .map(pull -> List.of(
                                pull.getOrDefault("red", 0),
                                pull.getOrDefault("green", 0),
                                pull.getOrDefault("blue", 0)))
                        .reduce(List.of(-1, -1, -1), (list1, list2) -> List.of(
                                Math.max(list1.get(0), list2.get(0)),
                                Math.max(list1.get(1), list2.get(1)),
                                Math.max(list1.get(2), list2.get(2))))
                        .stream()
                        .reduce(1, Math::multiplyExact))
                .sum();
    }
}
