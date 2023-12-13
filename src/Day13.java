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
        System.out.println(part1(input)); // 37975
        System.out.println(part2(input)); // 32497
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
        List<List<List<Character>>> rotatedPatches = rotatePatches(patches);
        for (int i = 0; i < patches.size(); i++) {
            long result = 100L * getReflectionLine(patches.get(i), 0);
            if (result == 0)
                result = getReflectionLine(rotatedPatches.get(i), 0);
            sum.addAndGet(result);
        }
        return sum.get();
    }

    public static long part2(List<List<List<Character>>> patches) {
        AtomicLong sum = new AtomicLong();
        List<List<List<Character>>> rotatedPatches = rotatePatches(patches);
        for (int i = 0; i < patches.size(); i++) {
            long result = 100L * getReflectionLine(patches.get(i), 1);
            if (result == 0)
                result = getReflectionLine(rotatedPatches.get(i), 1);
            sum.addAndGet(result);
        }
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
