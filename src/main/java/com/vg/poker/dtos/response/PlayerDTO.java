package com.vg.poker.dtos.response;

import com.vg.poker.entity.Card;
import lombok.Data;

import java.util.List;

@Data
public class PlayerDTO {
    private String playerId;
    private String name;
    private int chips;
    private List<Card> hand;
    private boolean folded;
    private int currentRoundBet;
    private boolean actedThisRound;
}
