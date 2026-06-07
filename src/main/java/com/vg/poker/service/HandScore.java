package com.vg.poker.service;

import com.vg.poker.entity.enums.HandRank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HandScore implements Comparable<HandScore> {

    private HandRank handRank;
    /**
     * Tie-breaker values, in descending order of importance.
     * Example: [pairRank, kicker1, kicker2, kicker3].
     */
    private List<Integer> tieBreakers;

    @Override
    public int compareTo(HandScore other) {
        int diff = Integer.compare(this.handRank.getStrength(), other.handRank.getStrength());
        if (diff != 0) {
            return diff;
        }

        int maxSize = Math.max(
                this.tieBreakers != null ? this.tieBreakers.size() : 0,
                other.tieBreakers != null ? other.tieBreakers.size() : 0
        );

        for (int i = 0; i < maxSize; i++) {
            int a = (this.tieBreakers != null && i < this.tieBreakers.size())
                    ? this.tieBreakers.get(i) : 0;
            int b = (other.tieBreakers != null && i < other.tieBreakers.size())
                    ? other.tieBreakers.get(i) : 0;

            int t = Integer.compare(a, b);
            if (t != 0) {
                return t;
            }
        }
        return 0;
    }
}