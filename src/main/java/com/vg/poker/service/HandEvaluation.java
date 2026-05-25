package com.vg.poker.service;

import com.vg.poker.entity.Card;
import com.vg.poker.entity.enums.HandRank;

import java.util.*;
import java.util.stream.Collectors;

public class HandEvaluation {

    /**
     * Evaluate the best 5-card hand out of the given cards.
     */
    public static HandScore evaluate(List<Card> cards) {
        if (cards == null || cards.size() < 5) {
            // can't really evaluate, treat as worst possible
            return new HandScore(HandRank.HIGH_CARD, List.of(0));
        }

        // --- Build rank & suit counts ---
        Map<Integer, Integer> rankCountsMap = new HashMap<>();
        Map<String, List<Integer>> suitToRanksMap = new HashMap<>();

        for (Card c : cards) {
            int rv = rankValue(c.getRank());
            rankCountsMap.merge(rv, 1, Integer::sum);

            suitToRanksMap.computeIfAbsent(c.getSuit(), s -> new ArrayList<>())
                    .add(rv);
        }

        // sorted unique ranks (desc) for straight detection & kickers
        List<Integer> uniqueRanksDesc = rankCountsMap.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        // --- Check for flush & straight flush ---
        String flushSuit = null;
        List<Integer> flushRanksDesc = null;

        for (Map.Entry<String, List<Integer>> entry : suitToRanksMap.entrySet()) {
            if (entry.getValue().size() >= 5) {
                flushSuit = entry.getKey();
                flushRanksDesc = entry.getValue().stream()
                        .distinct()
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList());
                break;
            }
        }

        // Straight flush / Royal flush
        if (flushSuit != null) {
            Integer straightFlushHigh = findStraightHigh(flushRanksDesc);
            if (straightFlushHigh != null) {
                if (straightFlushHigh == 14 && flushRanksDesc.containsAll(List.of(14, 13, 12, 11, 10))) {
                    // 10,J,Q,K,A of same suit
                    return new HandScore(HandRank.ROYAL_FLUSH, List.of(14));
                } else {
                    return new HandScore(HandRank.STRAIGHT_FLUSH, List.of(straightFlushHigh));
                }
            }
        }

        // --- Four of a kind / Full house / Trips / Pairs ---
        List<Integer> fours = new ArrayList<>();
        List<Integer> threes = new ArrayList<>();
        List<Integer> pairs = new ArrayList<>();
        List<Integer> singles = new ArrayList<>();

        for (int rank : rankCountsMap.keySet()) {
            int count = rankCountsMap.get(rank);
            if (count == 4) {
                fours.add(rank);
            } else if (count == 3) {
                threes.add(rank);
            } else if (count == 2) {
                pairs.add(rank);
            } else {
                singles.add(rank);
            }
        }

        fours.sort(Comparator.reverseOrder());
        threes.sort(Comparator.reverseOrder());
        pairs.sort(Comparator.reverseOrder());
        singles.sort(Comparator.reverseOrder());

        // Four of a kind
        if (!fours.isEmpty()) {
            int four = fours.get(0);
            int kicker = highestExcluding(uniqueRanksDesc, List.of(four));
            return new HandScore(
                    HandRank.FOUR_OF_A_KIND,
                    List.of(four, kicker)
            );
        }

        // Full house
        if (!threes.isEmpty() && (!pairs.isEmpty() || threes.size() > 1)) {
            int three = threes.get(0);
            int pairLike;
            if (threes.size() > 1) {
                if(pairs.isEmpty() || (threes.get(1)> pairs.get(0))) pairLike = threes.get(1);
                else pairLike = pairs.get(0);
            } else {
                pairLike = pairs.get(0);
            }
            return new HandScore(
                    HandRank.FULL_HOUSE,
                    List.of(three, pairLike)
            );
        }

        // Flush (no straight flush, we checked earlier)
        if (flushSuit != null && flushRanksDesc.size() >= 5) {
            List<Integer> top5 = flushRanksDesc.stream().limit(5).collect(Collectors.toList());
            return new HandScore(HandRank.FLUSH, top5);
        }

        // Straight (no flush)
        Integer straightHigh = findStraightHigh(uniqueRanksDesc);
        if (straightHigh != null) {
            return new HandScore(HandRank.STRAIGHT, List.of(straightHigh));
        }

        // Three of a kind
        if (!threes.isEmpty()) {
            int three = threes.get(0);
            List<Integer> kickers = topKickers(uniqueRanksDesc, List.of(three), 2);
            List<Integer> tb = new ArrayList<>();
            tb.add(three);
            tb.addAll(kickers);
            return new HandScore(HandRank.THREE_OF_A_KIND, tb);
        }

        // Two pair
        if (pairs.size() >= 2) {
            int highPair = pairs.get(0);
            int lowPair = pairs.get(1);
            int kicker = highestExcluding(uniqueRanksDesc, List.of(highPair, lowPair));
            return new HandScore(
                    HandRank.TWO_PAIR,
                    List.of(highPair, lowPair, kicker)
            );
        }

        // One pair
        if (pairs.size() == 1) {
            int pair = pairs.get(0);
            List<Integer> kickers = topKickers(uniqueRanksDesc, List.of(pair), 3);
            List<Integer> tb = new ArrayList<>();
            tb.add(pair);
            tb.addAll(kickers);
            return new HandScore(HandRank.ONE_PAIR, tb);
        }

        // High card
        List<Integer> top5 = uniqueRanksDesc.stream().limit(5).collect(Collectors.toList());
        return new HandScore(HandRank.HIGH_CARD, top5);
    }

    private static int rankValue(String rank) {
        return switch (rank) {
            case "J" -> 11;
            case "Q" -> 12;
            case "K" -> 13;
            case "A" -> 14;
            default -> Integer.parseInt(rank);
        };
    }

    /**
     * Find highest straight high-card from a list of ranks (desc, unique).
     */
    private static Integer findStraightHigh(List<Integer> ranksDesc) {
        if (ranksDesc.isEmpty()) return null;

        // Work on ascending for easier straight detection
        List<Integer> asc = new ArrayList<>(ranksDesc);
        Collections.sort(asc); // ascending

        // Handle wheel (A-2-3-4-5) by treating Ace as 1 as well
        if (asc.contains(14)) {
            asc.add(0, 1); // Ace low
        }

        int bestHigh = -1;
        int currentLen = 1;

        for (int i = 1; i < asc.size(); i++) {
            if (asc.get(i) == asc.get(i - 1) + 1) {
                currentLen++;
                if (currentLen >= 5) {
                    bestHigh = asc.get(i);
                }
            } else if (!asc.get(i).equals(asc.get(i - 1))) {
                currentLen = 1;
            }
        }

        return bestHigh > 0 ? bestHigh : null;
    }

    private static int highestExcluding(List<Integer> ranksDesc, List<Integer> exclude) {
        for (int r : ranksDesc) {
            if (!exclude.contains(r)) {
                return r;
            }
        }
        return 0;
    }

    private static List<Integer> topKickers(List<Integer> ranksDesc, List<Integer> exclude, int limit) {
        List<Integer> result = new ArrayList<>();
        for (int r : ranksDesc) {
            if (!exclude.contains(r)) {
                result.add(r);
                if (result.size() == limit) break;
            }
        }
        return result;
    }
}