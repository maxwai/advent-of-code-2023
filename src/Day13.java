import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class Day13 {

    public static void main(String[] args) throws IOException {
        List<List<List<Character>>> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day13-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static List<List<List<Character>>> parseInput(Stream<String> lines) {
        List<List<List<Character>>> patches = new ArrayList<>();
        patches.add(new ArrayList<>());
        lines.forEach(line -> {
            if (line.isEmpty())
                patches.add(new ArrayList<>());
            else
                patches.get(patches.size() - 1)
                        .add(new ArrayList<>(line.chars().mapToObj(ch -> (char) ch).toList()));
        });
        return patches;
    }

    public static long part1(List<List<List<Character>>> patches) {
        AtomicLong sum = new AtomicLong();
        patches.forEach(patch -> sum.addAndGet(100L * getReflectionLine(patch, 0)));
        rotatePatches(patches).forEach(patch -> sum.addAndGet(getReflectionLine(patch, 0)));
        return sum.get();
    }

    public static long part2(List<List<List<Character>>> patches) {
        AtomicLong sum = new AtomicLong();
        patches.forEach(patch -> sum.addAndGet(100L * getReflectionLine(patch, 1)));
        rotatePatches(patches).forEach(patch -> sum.addAndGet(getReflectionLine(patch, 1)));
        return sum.get();
    }

    public static List<List<List<Character>>> rotatePatches(List<List<List<Character>>> patches) {
        List<List<List<Character>>> rotatedPatches = new ArrayList<>();
        patches.forEach(patch -> {
            List<List<Character>> rotatedPatch = new ArrayList<>();
            for (int j = 0; j < patch.get(0).size(); j++) {
                List<Character> rotatedLine = new ArrayList<>();
                for (List<Character> characters : patch)
                    rotatedLine.add(characters.get(j));
                rotatedPatch.add(rotatedLine);
            }
            rotatedPatches.add(rotatedPatch);
        });
        return rotatedPatches;
    }

    public static long getReflectionLine(List<List<Character>> patch, int wantedDifferences) {
        for (int i = 0; i < patch.size() - 1; i++) {
            int allowedDifferences = wantedDifferences;
            for (int j = 0; j <= i && j < patch.size() - i - 1; j++) {
                for (int x = 0; x < patch.get(i - j).size(); x++) {
                    if (!patch.get(i - j).get(x).equals(patch.get(i + 1 + j).get(x)))
                        allowedDifferences--;
                }

                if (allowedDifferences < 0)
                    break;
            }
            if (allowedDifferences == 0)
                return i + 1;
        }
        return 0;
    }
}
