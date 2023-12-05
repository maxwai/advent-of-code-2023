import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5 {

    public static void main(String[] args) throws IOException {
        List<Map<Range, Range>> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day5-input.txt"))) {
            input = parseInput(reader.lines());
        }
        List<Long> seeds;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day5-input.txt"))) {
            seeds = parseSeeds(reader.lines());
        }
        System.out.println(part1(seeds, input).start); // 3374647
        List<Range> seedRanges;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day5-input.txt"))) {
            seedRanges = parseSeedRange(reader.lines());
        }
        System.out.println(part2(seedRanges, input).start);
    }

    public static List<Long> parseSeeds(Stream<String> lines) {
        return lines.filter(line -> line.startsWith("seeds:"))
                .flatMap(line -> Arrays.stream(line.substring(7).split(" "))
                        .map(Long::parseLong)
                        .toList()
                        .stream())
                .collect(Collectors.toList());
    }

    public static List<Range> parseSeedRange(Stream<String> lines) {
        List<Long> seeds = parseSeeds(lines);
        List<Range> seedRange = new ArrayList<>();
        for (int i = 0; i < seeds.size() - 1; i += 2) {
            seedRange.add(new Range(seeds.get(i), seeds.get(i + 1)));
        }
        if (seeds.size() % 2 != 0) {
            seedRange.add(new Range(seeds.get(seeds.size() - 1)));
        }
        return seedRange;
    }

    public static List<Map<Range, Range>> parseInput(Stream<String> lines) {
        Map<Range, Range> seedToSoil = new HashMap<>();
        Map<Range, Range> soilToFertilizer = new HashMap<>();
        Map<Range, Range> fertilizerToWater = new HashMap<>();
        Map<Range, Range> waterToLight = new HashMap<>();
        Map<Range, Range> lightToTemperature = new HashMap<>();
        Map<Range, Range> temperatureToHumidity = new HashMap<>();
        Map<Range, Range> humidityToLocation = new HashMap<>();

        AtomicReference<Map<Range, Range>> active = new AtomicReference<>();

        lines.forEach(line -> {
            if (line.startsWith("seeds:"))
                return;
            switch (line) {
                case "seed-to-soil map:" -> active.set(seedToSoil);
                case "soil-to-fertilizer map:" -> active.set(soilToFertilizer);
                case "fertilizer-to-water map:" -> active.set(fertilizerToWater);
                case "water-to-light map:" -> active.set(waterToLight);
                case "light-to-temperature map:" -> active.set(lightToTemperature);
                case "temperature-to-humidity map:" -> active.set(temperatureToHumidity);
                case "humidity-to-location map:" -> active.set(humidityToLocation);
                case "" -> {
                }
                default -> {
                    String[] numbers = line.split(" ");
                    active.get().put(new Range(Long.parseLong(numbers[1]), Integer.parseInt(numbers[2])),
                            new Range(Long.parseLong(numbers[0]), Integer.parseInt(numbers[2])));
                }
            }
        });

        return List.of(seedToSoil,
                soilToFertilizer,
                fertilizerToWater,
                waterToLight,
                lightToTemperature,
                temperatureToHumidity,
                humidityToLocation);
    }

    public static Stream<Range> mapToRange(Range initialRange, Map<Range, Range> map) {
        return map.entrySet()
                .stream()
                .filter(entry -> entry.getKey().overlap(initialRange))
                .map(Map.Entry::getValue);
    }

    public static Range part1(List<Long> seeds, List<Map<Range, Range>> mappings) {
        return seeds.stream()
                .flatMap(seed -> Range.mapRange(seed, mappings.get(0)).stream()).distinct()
                .flatMap(range -> range.mapRange(mappings.get(1)).stream()).distinct()
                .flatMap(range -> range.mapRange(mappings.get(2)).stream()).distinct()
                .flatMap(range -> range.mapRange(mappings.get(3)).stream()).distinct()
                .flatMap(range -> range.mapRange(mappings.get(4)).stream()).distinct()
                .flatMap(range -> range.mapRange(mappings.get(5)).stream()).distinct()
                .flatMap(range -> range.mapRange(mappings.get(6)).stream()).distinct()
                .min(Range::compareTo)
                .orElse(null);
    }


    public static Range part2(List<Range> seeds, List<Map<Range, Range>> mappings) {
         return seeds.stream()
                .flatMap(seed -> seed.mapRange(mappings.get(0)).stream()).distinct()
                .flatMap(range -> range.mapRange(mappings.get(1)).stream()).distinct()
                .flatMap(range -> range.mapRange(mappings.get(2)).stream()).distinct()
                .flatMap(range -> range.mapRange(mappings.get(3)).stream()).distinct()
                .flatMap(range -> range.mapRange(mappings.get(4)).stream()).distinct()
                .flatMap(range -> range.mapRange(mappings.get(5)).stream()).distinct()
                .flatMap(range -> range.mapRange(mappings.get(6)).stream()).distinct()
                .min(Range::compareTo)
                .orElse(null);
    }

    public static class Range implements Comparable<Range> {

        final long start;
        final long stop;

        public Range(long start, long stop, Object ignored) {
            this.start = start;
            this.stop = stop;
            if (start < 0 || stop < 0)
                System.err.println("Ranges are negative");
        }

        public Range(long start) {
            this(start, start, null);
        }

        public Range(long start, long range) {
            this(start, start + range - 1, null);
        }

        public static List<Range> mapRange(Long value, Map<Range, Range> mappings) {
            return mappings.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().contains(value))
                    .map(entry -> entry.getKey().reduce(value, entry.getValue()))
                    .toList();
        }

        public List<Range> mapRange(Map<Range, Range> mappings) {
            return mappings.entrySet()
                    .stream()
                    .filter(entry -> overlap(entry.getKey()))
                    .map(entry -> reduce(entry.getKey(), entry.getValue()))
                    .toList();
        }

        public Range reduce(Range compare, Range goal) {
            if (!this.overlap(compare))
                throw new IllegalStateException();
            long start = goal.start;
            long stop = goal.stop;
            if (compare.start < this.start) {
                start += this.start - compare.start;
            }
            if (compare.stop > this.stop) {
                stop -= compare.stop - this.stop;
            }
            return new Range(start, stop, null);
        }

        public Range reduce(Long original, Range goal) {
            if (!this.contains(original))
                throw new IllegalStateException();
            return new Range(goal.start + original - this.start);
        }

        public boolean overlap(Range range) {
            return !(stop < range.start || start > range.stop);
        }

        public boolean contains(Long value) {
            return start <= value && value <= stop;
        }

        @Override
        public int compareTo(Range o) {
            return Long.compare(start, o.start);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof Range other) {
                return start == other.start && stop == other.stop;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, stop);
        }

        @Override
        public String toString() {
            if (start == stop)
                return "[" + start + "]";
            return "[" + start + ", " + stop + "] (" + (stop - start + 1) + " items)";
        }
    }

}
