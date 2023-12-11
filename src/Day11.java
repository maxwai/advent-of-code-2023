import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Day11 {

    public static void main(String[] args) throws IOException {
        List<List<Boolean>> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day11-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static List<List<Boolean>> parseInput(Stream<String> lines) {
        List<List<Boolean>> stars = new ArrayList<>();
        lines.forEach(line -> stars.add(new ArrayList<>(line.chars()
                .mapToObj(ch -> ch == '#')
                .toList())));

        return stars;
    }

    public static long part1(List<List<Boolean>> stars) {
        return getDistances(growUniverse(stars, 1));
    }

    public static long part2(List<List<Boolean>> stars) {
        return getDistances(growUniverse(stars, 1_000_000 - 1));
    }

    public static List<Coords> getStars(List<List<Boolean>> stars) {
        List<Coords> starCoords = new ArrayList<>();
        for (int i = 0; i < stars.size(); i++) {
            for (int j = 0; j < stars.get(i).size(); j++) {
                if (stars.get(i).get(j)) {
                    starCoords.add(new Coords(i, j));
                }
            }
        }
        return starCoords;
    }

    public static long getDistances(List<Coords> stars) {
        long sum = 0;
        for (int i = 0; i < stars.size(); i++) {
            for (int j = i + 1; j < stars.size(); j++) {
                sum += Math.abs(stars.get(i).x - stars.get(j).x) + Math.abs(stars.get(i).y - stars.get(j).y);
            }
        }
        return sum;
    }

    public static List<Coords> growUniverse(List<List<Boolean>> stars, int shift) {
        List<Coords> starCoords = getStars(stars);

        AtomicInteger horizontalShift = new AtomicInteger();
        AtomicInteger verticalShift = new AtomicInteger();
        for (int i = 0; i < stars.size(); i++) {
            int finalI = i;
            if (stars.get(i).stream().noneMatch(b -> b)) {
                starCoords.stream()
                        .filter(star -> star.x > finalI + verticalShift.get())
                        .forEach(star -> star.x = star.x + shift);
                verticalShift.addAndGet(shift);
            }
            if (stars.stream().noneMatch(line -> line.get(finalI))) {
                starCoords.stream()
                        .filter(star -> star.y > finalI + horizontalShift.get())
                        .forEach(star -> star.y = star.y + shift);
                horizontalShift.addAndGet(shift);
            }
        }
        return starCoords;
    }

    public static final class Coords {
        public long x;
        public long y;

        public Coords(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }
}
