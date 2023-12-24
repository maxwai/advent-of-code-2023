import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day24 {

    public static void main(String[] args) throws IOException {
        List<Function> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("inputs/Day24-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input, 200000000000000L, 400000000000000L));
        // Part 2 see python
    }

    public static List<Function> parseInput(Stream<String> lines) {
        List<Function> functions = new ArrayList<>();
        lines.forEach(line -> {
            String[] split = line.split("@");
            String[] point = split[0].split(",");
            String[] velocity = split[1].split(",");
            long x1 = Long.parseLong(point[0].trim());
            long y1 = Long.parseLong(point[1].trim());
            long z1 = Long.parseLong(point[2].trim());
            int dx = Integer.parseInt(velocity[0].trim());
            int dy = Integer.parseInt(velocity[1].trim());
            int dz = Integer.parseInt(velocity[2].trim());
            functions.add(new Function(dx, dy, dz, x1, y1, z1));
        });
        return functions;
    }

    public static long part1(List<Function> functions, long min, long max) {
        return functions.stream()
                .mapToLong(f -> functions.stream()
                        .skip(functions.indexOf(f) + 1)
                        .filter(f2 -> {
                            int denom = f.dx * f2.dy - f.dy * f2.dx;
                            if (denom == 0) {
                                return false;
                            }
                            double t = ((f2.x1 - f.x1) * f2.dy - (f2.y1 - f.y1) * f2.dx) / (double) denom;
                            double u = ((f2.x1 - f.x1) * f.dy - (f2.y1 - f.y1) * f.dx) / (double) denom;
                            return t >= 0 && u >= 0 && min <= f.x1 + t * f.dx && f.x1 + t * f.dx <= max &&
                                   min <= f.y1 + t * f.dy && f.y1 + t * f.dy <= max;
                        })
                        .count())
                .sum();
    }

    public record Function(int dx, int dy, int dz, long x1, long y1, long z1) {
    }
}
