package com.vg.poker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    @Id
    private String id;
    private String name;
    @Builder.Default
    private int chips=0;
    @Builder.Default
    private List<Card> hand = new ArrayList<>();
    @Builder.Default
    private boolean folded = false;
    @Builder.Default
    private int currentRoundBet = 0;
    @Builder.Default
    private boolean actedThisRound = false;
}
