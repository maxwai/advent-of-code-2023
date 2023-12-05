import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class Day5 {

    public static void main(String[] args) throws IOException {
        List<CustomMap> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day5-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static List<CustomMap> parseInput(Stream<String> lines) {
        CustomMap seedToSoil = new CustomMap();
        CustomMap soilToFertilizer = new CustomMap();
        CustomMap fertilizerToWater = new CustomMap();
        CustomMap waterToLight = new CustomMap();
        CustomMap lightToTemperature = new CustomMap();
        CustomMap temperatureToHumidity = new CustomMap();
        CustomMap humidityToLocation = new CustomMap();

        AtomicReference<CustomMap> active = new AtomicReference<>();
        List<Long> initialSeeds = new ArrayList<>();

        lines.forEach(line -> {
            if (line.startsWith("seeds:")) {
                initialSeeds.addAll(Arrays.stream(line.substring(7).split(" ")).map(Long::parseLong).toList());
                return;
            }
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
                    active.get().put(Long.parseLong(numbers[1]), Long.parseLong(numbers[0]), Integer.parseInt(numbers[2]));
                }
            }
        });

        seedToSoil.restrict(initialSeeds);

        return List.of(seedToSoil,
                soilToFertilizer,
                fertilizerToWater,
                waterToLight,
                lightToTemperature,
                temperatureToHumidity,
                humidityToLocation);
    }

    public static long part1(List<CustomMap> cards) {
        return cards.get(0)
                .values()
                .stream()
                .mapToLong(aLong -> cards.get(1).get(aLong))
                .map(aLong -> cards.get(2).get(aLong))
                .map(aLong -> cards.get(3).get(aLong))
                .map(aLong -> cards.get(4).get(aLong))
                .map(aLong -> cards.get(5).get(aLong))
                .map(aLong -> cards.get(6).get(aLong))
                .min()
                .orElse(-1);
    }


    public static int part2(List<CustomMap> cards) {
        return 0;
    }

    public static class CustomMap implements Map<Long, Long> {

        private final Map<Long, Integer> sourceMap = new TreeMap<>();
        private final Map<Long, Integer> destinationMap = new TreeMap<>();
        private final Map<Long, Long> sourceDestinationMap = new TreeMap<>();

        public Entry<Long, Long> getEntry(Long key) {
            for (Entry<Long, Integer> entry : sourceMap.entrySet()) {
                if (entry.getKey() <= key && key < entry.getKey() + entry.getValue()) {
                    return sourceDestinationMap.entrySet()
                            .stream()
                            .filter(entry1 -> entry1.getKey().equals(entry.getKey()))
                            .findAny()
                            .orElse(null);
                }
            }
            return null;
        }

        private boolean contains(Map<Long, Integer> map, Long key) {
            for (Entry<Long, Integer> entry : map.entrySet()) {
                if (entry.getKey() <= key && entry.getKey() + entry.getValue() > key)
                    return true;
            }
            return false;
        }

        public void put(Long key, Long value, Integer range) {
            sourceMap.put(key, range);
            destinationMap.put(value, range);
            sourceDestinationMap.put(key, value);
        }

        @Override
        public int size() {
            return sourceMap.size();
        }

        @Override
        public boolean isEmpty() {
            return sourceMap.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            if (key instanceof Long longKey)
                return contains(sourceMap, longKey);
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            if (value instanceof Long longValue)
                return contains(destinationMap, longValue);
            return false;
        }

        @Override
        public Long get(Object key) {
            if (key instanceof Long longKey) {
                Entry<Long, Long> entry = getEntry(longKey);
                return entry == null ? longKey : entry.getValue() + longKey - entry.getKey();
            }
            return null;
        }

        @Override
        @Deprecated
        public Long put(Long key, Long value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Long remove(Object key) {
            if (key instanceof Long longKey) {
                Entry<Long, Long> entry = getEntry(longKey);
                if (entry == null)
                    return null;
                sourceMap.remove(entry.getKey());
                destinationMap.remove(entry.getValue());
                sourceDestinationMap.remove(entry.getKey());
                return entry.getValue();
            }
            return null;
        }

        @Override
        @Deprecated
        public void putAll(Map<? extends Long, ? extends Long> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            sourceMap.clear();
            destinationMap.clear();
            sourceDestinationMap.clear();
        }

        public void restrict(List<Long> keys) {
            Map<Long, Long> mappings = new HashMap<>();
            Set<Long> realValues = new HashSet<>();

            keys.forEach(key -> {
                Entry<Long, Long> entry = getEntry(key);
                if (entry == null)
                    return;
                long value = entry.getValue() + key - entry.getKey();
                realValues.add(value);
                sourceMap.put(key, 1);
                destinationMap.put(value, 1);
                mappings.put(key, value);
            });

            sourceMap.keySet().retainAll(keys);
            destinationMap.keySet().retainAll(realValues);
            sourceDestinationMap.clear();
            sourceDestinationMap.putAll(mappings);
        }

        @Override
        @Deprecated
        public Set<Long> keySet() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Long> values() {
            if (destinationMap.values().stream().anyMatch(i -> i != 1))
                throw new UnsupportedOperationException();
            return sourceDestinationMap.values();
        }

        @Override
        public Set<Entry<Long, Long>> entrySet() {
            if (sourceMap.values().stream().anyMatch(i -> i != 1))
                throw new UnsupportedOperationException();
            return sourceDestinationMap.entrySet();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof CustomMap other) {
                return sourceMap.equals(other.sourceMap)
                       && destinationMap.equals(other.destinationMap)
                       && sourceDestinationMap.equals(other.sourceDestinationMap);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(sourceMap, destinationMap, sourceDestinationMap);
        }
    }

}
