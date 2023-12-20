import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class Day20 {

    public static void main(String[] args) throws IOException {
        Map<String, Module> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("inputs/Day20-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static Map<String, Module> parseInput(Stream<String> lines) {
        Map<String, Module> modules = new HashMap<>();
        lines.forEach(line -> {
            if (line.startsWith("broadcaster")) {
                modules.put("broadcaster", new Broadcaster("broadcaster",
                        Arrays.stream(line.split("->")[1].split(","))
                                .map(String::trim)
                                .toList()));
            } else if (line.startsWith("%")) {
                String[] split = line.split("->");
                String name = split[0].substring(1).trim();
                modules.put(name, new FlipFlop(name, Arrays.stream(split[1].split(","))
                        .map(String::trim)
                        .toList()));
            } else {
                String[] split = line.split("->");
                String name = split[0].substring(1).trim();
                modules.put(name, new Conjunction(name, Arrays.stream(split[1].split(","))
                        .map(String::trim)
                        .toList()));
            }
        });
        modules.forEach((name, module) -> {
            if (module instanceof Conjunction conjunction) {
                modules.forEach((name1, module1) -> {
                    if (module1.outputs.contains(name)) {
                        conjunction.addInput(name1);
                    }
                });
            }
        });
        return modules;
    }

    public static long part1(Map<String, Module> modules) {
        Broadcaster broadcaster = (Broadcaster) modules.get("broadcaster");
        List<Signal> signals = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            signals.addAll(broadcaster.signal(null, false));
            while (!signals.isEmpty()) {
                Signal signal = signals.remove(0);
                Module destModule = modules.get(signal.dest);
                if (destModule != null)
                    signals.addAll(destModule.signal(signal.src, signal.high));
            }
        }
        long low = modules.values()
                           .stream()
                           .mapToLong(module -> module.sendSignals.getOrDefault(false, 0))
                           .sum() + 1000;
        long high = modules.values()
                .stream()
                .mapToLong(module -> module.sendSignals.getOrDefault(true, 0))
                .sum();
        return low * high;
    }

    public static long part2(Map<String, Module> modules) {
        // we want to check when cs send a low signal, for that every input must have send a high signal before that
        // there are 4 inputs to cs: lz, tg, kh, hn
        // after looking at the circuit graph, we can see that these four are each the output of a loop
        // the loops are:
        // kc -> ... -> lz
        // gv -> ... -> tg
        // hv -> ... -> kh
        // fm -> ... -> hn
        // Let's find out, for each, when these loops loop

        modules.values().forEach(Module::reset);
        Broadcaster broadcaster = (Broadcaster) modules.get("broadcaster");
        Conjunction cs = (Conjunction) modules.get("cs");
        int lz, tg, kh, hn;
        lz = tg = kh = hn = 0;
        List<Signal> signals = new ArrayList<>();
        for (int i = 0; true; i++) {
            signals.addAll(broadcaster.signal(null, false));
            while (!signals.isEmpty()) {
                Signal signal = signals.remove(0);
                Module destModule = modules.get(signal.dest);
                if (destModule != null) {
                    signals.addAll(destModule.signal(signal.src, signal.high));
                }
                if (lz == 0 && cs.inputs.get("lz")) {
                    lz = i + 1;
                }
                if (tg == 0 && cs.inputs.get("tg")) {
                    tg = i + 1;
                }
                if (kh == 0 && cs.inputs.get("kh")) {
                    kh = i + 1;
                }
                if (hn == 0 && cs.inputs.get("hn")) {
                    hn = i + 1;
                }
            }
            if (lz != 0 && tg != 0 && kh != 0 && hn != 0) {
                break;
            }
        }
        return lcm(lz, tg, kh, hn);
    }

    private static long gcd(long a, long b) {
        while (b > 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public static long lcm(long... numbers) {
        return Arrays.stream(numbers).reduce(1, (x, y) -> x * (y / gcd(x, y)));
    }


    public record Signal(String src, String dest, boolean high) {
        @Override
        public String toString() {
            return src + " -" + (high ? "high" : "low") + "-> " + dest;
        }
    }

    public static abstract class Module {
        public final String name;
        public final List<String> outputs;
        public final Map<Boolean, Integer> sendSignals = new HashMap<>();

        public Module(String name, List<String> outputs) {
            this.name = name;
            this.outputs = Collections.unmodifiableList(outputs);
        }

        public abstract List<Signal> signal(String src, boolean high);

        public void reset() {
            sendSignals.clear();
        }

    }

    public static class FlipFlop extends Module {
        public boolean state = false;

        public FlipFlop(String name, List<String> outputs) {
            super(name, outputs);
        }

        @Override
        public List<Signal> signal(String src, boolean high) {
            if (high) {
                return Collections.emptyList();
            }
            state = !state;
            sendSignals.put(state, sendSignals.getOrDefault(state, 0) + outputs.size());
            return outputs.stream()
                    .map(output -> new Signal(name, output, state))
                    .toList();
        }

        @Override
        public void reset() {
            super.reset();
            state = false;
        }
    }

    public static class Conjunction extends Module {
        public final Map<String, Boolean> inputs = new HashMap<>();

        public Conjunction(String name, List<String> outputs) {
            super(name, outputs);
        }

        public void addInput(String input) {
            inputs.put(input, false);
        }

        @Override
        public List<Signal> signal(String src, boolean high) {
            inputs.put(src, high);
            boolean output = inputs.values().stream().anyMatch(b -> !b);
            sendSignals.put(output, sendSignals.getOrDefault(output, 0) + outputs.size());
            return outputs.stream()
                    .map(output1 -> new Signal(name, output1, output))
                    .toList();
        }

        @Override
        public void reset() {
            super.reset();
            inputs.entrySet().forEach(entry -> entry.setValue(false));
        }
    }

    public static class Broadcaster extends Module {
        public Broadcaster(String name, List<String> outputs) {
            super(name, outputs);
        }

        @Override
        public List<Signal> signal(String src, boolean high) {
            sendSignals.put(high, sendSignals.getOrDefault(high, 0) + outputs.size());
            return outputs.stream()
                    .map(output -> new Signal(name, output, high))
                    .toList();
        }
    }
}
