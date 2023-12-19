import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19 {

    public static void main(String[] args) throws IOException {
        ParsingOutput input;
        try (BufferedReader reader = new BufferedReader(new FileReader("inputs/Day19-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static ParsingOutput parseInput(Stream<String> lines) {
        Map<String, Workflow> workflows = new HashMap<>();
        List<Part> parts = new ArrayList<>();
        AtomicBoolean part1 = new AtomicBoolean(true);
        lines.forEach(line -> {
            if (line.isEmpty()) {
                part1.set(false);
                return;
            }
            if (part1.get()) {
                String[] split = line.split("\\{");
                String[] functionStrings = split[1].substring(0, split[1].length() - 1).split(",");
                List<Function<Part, String>> functions = new ArrayList<>();
                List<Character> variables = new ArrayList<>();
                for (String functionString : functionStrings) {
                    if (!functionString.contains(":")) {
                        functions.add(part -> functionString);
                        variables.add(null);
                        continue;
                    }
                    char var = functionString.charAt(0);
                    variables.add(var);
                    char operation = functionString.charAt(1);
                    String[] rest = functionString.substring(2).split(":");
                    int wantedValue = Integer.parseInt(rest[0]);
                    String goal = rest[1];
                    functions.add(part -> {
                        int value = switch (var) {
                            case 'x' -> part.x;
                            case 'm' -> part.m;
                            case 'a' -> part.a;
                            case 's' -> part.s;
                            default -> throw new IllegalStateException();
                        };
                        boolean result = switch (operation) {
                            case '<' -> value < wantedValue;
                            case '>' -> value > wantedValue;
                            default -> throw new IllegalStateException();
                        };
                        return result ? goal : null;
                    });
                }
                workflows.put(split[0], new Workflow(split[0], functions, variables));
            } else {
                line = line.substring(1, line.length() - 1);
                String[] values = line.split(",");
                int x = Integer.parseInt(values[0].split("=")[1]);
                int m = Integer.parseInt(values[1].split("=")[1]);
                int a = Integer.parseInt(values[2].split("=")[1]);
                int s = Integer.parseInt(values[3].split("=")[1]);
                parts.add(new Part(x, m, a, s));
            }
        });
        return new ParsingOutput(workflows, parts);
    }

    public static long part1(ParsingOutput setup) {
        return setup.parts.stream().filter(part -> {
            String result = "in";
            while (!result.equals("R") && !result.equals("A")) {
                result = setup.workflows.get(result).applyWorkflow(part);
            }
            return result.equals("A");
        }).mapToInt(part -> part.x + part.m + part.a + part.s).sum();
    }

    public static long part2(ParsingOutput setup) {
        Set<RangeOutput> results = new HashSet<>(setup.workflows.get("in").applyWorkflow(new PartRange()));
        Set<RangeOutput> valid = new HashSet<>();
        do {
            results.removeIf(rangeOutput -> {
                if (rangeOutput.nextWorkflow.equals("A")) {
                    valid.add(rangeOutput);
                    return true;
                }
                return false;
            });
            results = results.stream()
                    .flatMap(rangeOutput -> setup.workflows
                            .get(rangeOutput.nextWorkflow)
                            .applyWorkflow(rangeOutput.partRange)
                            .stream())
                    .filter(rangeOutput -> !rangeOutput.nextWorkflow.equals("R"))
                    .collect(Collectors.toSet());
        } while (!results.isEmpty());


        return valid.stream()
                .map(RangeOutput::partRange)
                .mapToLong(PartRange::size)
                .sum();
    }

    public record ParsingOutput(Map<String, Workflow> workflows, List<Part> parts) {
    }

    public record Part(int x, int m, int a, int s) {
    }

    public static final class PartRange {
        public int minX, maxX;
        public int minM, maxM;
        public int minA, maxA;
        public int minS, maxS;

        public PartRange(int minX, int maxX, int minM, int maxM, int minA, int maxA, int minS, int maxS) {
            this.minX = minX;
            this.maxX = maxX;
            this.minM = minM;
            this.maxM = maxM;
            this.minA = minA;
            this.maxA = maxA;
            this.minS = minS;
            this.maxS = maxS;
        }

        public PartRange(PartRange partRange) {
            this(partRange.minX, partRange.maxX,
                    partRange.minM, partRange.maxM,
                    partRange.minA, partRange.maxA,
                    partRange.minS, partRange.maxS);
        }

        public PartRange() {
            this(1, 4000,
                    1, 4000,
                    1, 4000,
                    1, 4000);
        }

        public boolean validInterval() {
            return minX <= maxX && minM <= maxM && minA <= maxA && minS <= maxS;
        }

        public long size() {
            return (long) (maxX - minX + 1) * (maxM - minM + 1) * (maxA - minA + 1) * (maxS - minS + 1);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (PartRange) obj;
            return this.minX == that.minX &&
                   this.maxX == that.maxX &&
                   this.minM == that.minM &&
                   this.maxM == that.maxM &&
                   this.minA == that.minA &&
                   this.maxA == that.maxA &&
                   this.minS == that.minS &&
                   this.maxS == that.maxS;
        }

        @Override
        public int hashCode() {
            return Objects.hash(minX, minM, minA, minS);
        }
    }

    public record RangeOutput(String nextWorkflow, PartRange partRange) {
    }

    public record Workflow(String name, List<Function<Part, String>> workflows, List<Character> variables) {
        public String applyWorkflow(Part part) {
            for (Function<Part, String> workflow : workflows) {
                String result = workflow.apply(part);
                if (result != null) return result;
            }
            throw new IllegalStateException();
        }

        public List<RangeOutput> applyWorkflow(PartRange partRange) {
            partRange = new PartRange(partRange);
            List<RangeOutput> outputs = new ArrayList<>();
            for (int i = 0; i < workflows.size(); i++) {
                PartRange copy = new PartRange(partRange);
                if (variables.get(i) == null) {
                    outputs.add(new RangeOutput(workflows.get(i).apply(new Part(0, 0, 0, 0)), copy));
                    continue;
                }
                while (copy.validInterval() && workflows.get(i).apply(new Part(
                        variables.get(i) == 'x' ? copy.minX : 0,
                        variables.get(i) == 'm' ? copy.minM : 0,
                        variables.get(i) == 'a' ? copy.minA : 0,
                        variables.get(i) == 's' ? copy.minS : 0)) == null) {
                    switch (variables.get(i)) {
                        case 'x' -> copy.minX++;
                        case 'm' -> copy.minM++;
                        case 'a' -> copy.minA++;
                        case 's' -> copy.minS++;
                    }
                }
                while (copy.validInterval() && workflows.get(i).apply(new Part(
                        variables.get(i) == 'x' ? copy.maxX : 0,
                        variables.get(i) == 'm' ? copy.maxM : 0,
                        variables.get(i) == 'a' ? copy.maxA : 0,
                        variables.get(i) == 's' ? copy.maxS : 0)) == null) {
                    switch (variables.get(i)) {
                        case 'x' -> copy.maxX--;
                        case 'm' -> copy.maxM--;
                        case 'a' -> copy.maxA--;
                        case 's' -> copy.maxS--;
                    }
                }
                if (copy.validInterval()) {
                    outputs.add(new RangeOutput(
                            workflows.get(i).apply(new Part(copy.maxX, copy.maxM, copy.maxA, copy.maxS)),
                            copy));
                    if (partRange.minX != copy.minX || partRange.maxX != copy.maxX) {
                        partRange.minX = partRange.minX == copy.minX ? copy.maxX + 1 : partRange.minX;
                        partRange.maxX = partRange.maxX == copy.maxX ? copy.minX - 1 : partRange.maxX;
                    } else if (partRange.minM != copy.minM || partRange.maxM != copy.maxM) {
                        partRange.minM = partRange.minM == copy.minM ? copy.maxM + 1 : partRange.minM;
                        partRange.maxM = partRange.maxM == copy.maxM ? copy.minM - 1 : partRange.maxM;
                    } else if (partRange.minA != copy.minA || partRange.maxA != copy.maxA) {
                        partRange.minA = partRange.minA == copy.minA ? copy.maxA + 1 : partRange.minA;
                        partRange.maxA = partRange.maxA == copy.maxA ? copy.minA - 1 : partRange.maxA;
                    } else if (partRange.minS != copy.minS || partRange.maxS != copy.maxS) {
                        partRange.minS = partRange.minS == copy.minS ? copy.maxS + 1 : partRange.minS;
                        partRange.maxS = partRange.maxS == copy.maxS ? copy.minS - 1 : partRange.maxS;
                    }
                }
            }
            return outputs;
        }
    }

}
