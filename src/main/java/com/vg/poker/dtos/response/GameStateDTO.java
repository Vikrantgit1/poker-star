package com.vg.poker.dtos.response;

import com.vg.poker.entity.Card;
import com.vg.poker.entity.enums.GamePhase;
import com.vg.poker.entity.enums.HandRank;
import com.vg.poker.entity.enums.PlayerActionType;
import lombok.Data;

import java.util.List;

@Data
public class GameStateDTO {
    private String gameId;
    private List<PlayerDTO> players;
    private List<Card> communityCards;
    private int pot;
    private GamePhase status;
    private String currentPlayerId;
    private int currentBet;
    private int minRaise;
    private List<PlayerActionType> legalActions;
    private List<String> winnerPlayerIds;
    private List<String> winnerNames;
    private HandRank winningHandRank;
    private boolean autoStartNextRound;
    private java.util.Map<String, Integer> lastRoundWinnings;
}
