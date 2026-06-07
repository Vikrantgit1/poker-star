package com.vg.poker.entity;

import com.vg.poker.entity.enums.GamePhase;
import com.vg.poker.entity.enums.HandRank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "game")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {
    @Id
    private String id;
    @Builder.Default
    private List<Player> players = new ArrayList<>();
    @Builder.Default
    private Deck deck = new Deck();
    @Builder.Default
    private List<Card> communityCards = new ArrayList<>();
    @Builder.Default
    private int pot = 0;
    @Builder.Default
    private GamePhase status = GamePhase.WAITING;
    private String currentPlayerId;
    @Builder.Default
    private int currentBet = 0;
    @Builder.Default
    private int minRaise = 1;
    @Builder.Default
    private List<String> winnerPlayerIds = new ArrayList<>();
    @Builder.Default
    private List<String> winnerNames = new ArrayList<>();
    private HandRank winningHandRank;
}
