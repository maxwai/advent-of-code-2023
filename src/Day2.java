import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Day2 {

    public static void main(String[] args) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("Day2-input.txt"))) {
            System.out.println(part1(reader.lines()));
        }
        try (BufferedReader reader = new BufferedReader(new FileReader("Day2-input.txt"))) {
            System.out.println(part2(reader.lines()));
        }
    }

    public static int part1(Stream<String> lines) {
        final int redCubes = 12;
        final int greenCubes = 13;
        final int blueCubes = 14;
        AtomicInteger sum = new AtomicInteger();
        lines.forEach(line -> {
            String[] split = line.split(":");
            int gameId = Integer.parseInt(split[0].split(" ")[1].trim());
            String[] balls = split[1].trim().split("[;,]");
            boolean possible = true;
            ballLoop:
            for (String ball : balls) {
                ball = ball.trim();
                String[] type = ball.split(" ");
                switch (type[1].trim()) {
                    case "red" -> {
                        if (Integer.parseInt(type[0].trim()) > redCubes) {
                            possible = false;
                            break ballLoop;
                        }
                    }
                    case "green" -> {
                        if (Integer.parseInt(type[0].trim()) > greenCubes) {
                            possible = false;
                            break ballLoop;
                        }
                    }
                    case "blue" -> {
                        if (Integer.parseInt(type[0].trim()) > blueCubes) {
                            possible = false;
                            break ballLoop;
                        }
                    }
                    default -> System.out.println("unknown color: " + ball);
                }
            }
            if (possible)
                sum.addAndGet(gameId);
        });
        return sum.get();
    }


    public static int part2(Stream<String> lines) {
        AtomicInteger sum = new AtomicInteger();
        lines.forEach(line -> {
            String[] split = line.split(":");
            String[] games = split[1].trim().split(";");
            int red = 0, blue = 0, green = 0;
            for (String game : games) {
                String[] balls = game.split(",");
                for (String ball : balls) {
                    ball = ball.trim();
                    String[] type = ball.split(" ");
                    int ballCount = Integer.parseInt(type[0].trim());
                    switch (type[1].trim()) {
                        case "red" -> {
                            if (ballCount > red)
                                red = ballCount;
                        }
                        case "green" -> {
                            if (ballCount > green)
                                green = ballCount;
                        }
                        case "blue" -> {
                            if (ballCount > blue)
                                blue = ballCount;
                        }
                        default -> System.out.println("unknown color: " + ball);
                    }
                }
            }
            sum.addAndGet(red *blue * green);
        });
        return sum.get();
    }
}
