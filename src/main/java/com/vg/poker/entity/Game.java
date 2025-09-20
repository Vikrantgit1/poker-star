package com.vg.poker.entity;

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
    private List<Player> players = new ArrayList<>();
    private Deck deck;
    private List<Card> communityCards = new ArrayList<>();
    private int pot = 0;
    private String status = "WAITING";
}
