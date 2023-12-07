import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class Day7 {

    public static void main(String[] args) throws IOException {
        List<Hand> input;
        try (BufferedReader reader = new BufferedReader(new FileReader("Day7-input.txt"))) {
            input = parseInput(reader.lines());
        }
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    public static List<Hand> parseInput(Stream<String> lines) {
        List<Hand> hands = new ArrayList<>();
        lines.forEach(line -> {
            String[] split = line.split(" ");
            char[] cards = split[0].toCharArray();
            hands.add(new Hand(cards[0], cards[1], cards[2], cards[3], cards[4], Integer.parseInt(split[1])));
        });
        return hands;
    }

    public static long part1(List<Hand> hands) {
        hands.sort(null);
        long sum = 0;
        for (int i = 0; i < hands.size(); i++) {
            sum += (long) (i + 1) * hands.get(i).bid;
        }
        return sum;
    }

    public static long part2(List<Hand> hands) {
        return 0;
    }

    public static class Hand implements Comparable<Hand> {

        public final char card1, card2, card3, card4, card5;
        public final int bid;
        public final Map<Character, Integer> cardAmounts = new HashMap<>();

        public Hand(char card1, char card2, char card3, char card4, char card5, int bid) {
            this.card1 = card1;
            this.card2 = card2;
            this.card3 = card3;
            this.card4 = card4;
            this.card5 = card5;
            this.bid = bid;
            cardAmounts.put(card1, cardAmounts.getOrDefault(card1, 0) + 1);
            cardAmounts.put(card2, cardAmounts.getOrDefault(card2, 0) + 1);
            cardAmounts.put(card3, cardAmounts.getOrDefault(card3, 0) + 1);
            cardAmounts.put(card4, cardAmounts.getOrDefault(card4, 0) + 1);
            cardAmounts.put(card5, cardAmounts.getOrDefault(card5, 0) + 1);
        }

        public static Comparator<Character> compareCard() {
            Map<Character, Integer> cardMappings = new HashMap<>(Map.of('2', 2, '3', 3, '4', 4, '5', 5, '6', 6, '7', 7, '8', 8, '9', 9, 'T', 10, 'J', 11));
            cardMappings.put('Q', 12);
            cardMappings.put('K', 13);
            cardMappings.put('A', 14);
            return Comparator.comparingInt(cardMappings::get);
        }

        public int type() {
            if (cardAmounts.containsValue(5)) {
                return 7;
            } else if (cardAmounts.containsValue(4)) {
                return 6;
            } else if (cardAmounts.containsValue(3) && cardAmounts.containsValue(2)) {
                return 5;
            } else if (cardAmounts.containsValue(3)) {
                return 4;
            } else if (cardAmounts.values().stream().filter(x -> x == 2).count() == 2) {
                return 3;
            } else if (cardAmounts.containsValue(2)) {
                return 2;
            } else {
                return 1;
            }
        }

        @Override
        public int compareTo(Hand o) {
            return Comparator.comparing(Hand::type)
                    .thenComparing(hand -> hand.card1, compareCard())
                    .thenComparing(hand -> hand.card2, compareCard())
                    .thenComparing(hand -> hand.card3, compareCard())
                    .thenComparing(hand -> hand.card4, compareCard())
                    .thenComparing(hand -> hand.card5, compareCard())
                    .compare(this, o);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof Hand other) {
                return bid == other.bid &&
                       Objects.equals(card1, other.card1) &&
                       Objects.equals(card2, other.card2) &&
                       Objects.equals(card3, other.card3) &&
                       Objects.equals(card4, other.card4) &&
                       Objects.equals(card5, other.card5);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return bid;
        }

        @Override
        public String toString() {
            return "" + card1 + card2 + card3 + card4 + card5 + " " + bid + " " + type();
        }
    }
}
