package com.vg.poker.dtos.response;

import com.vg.poker.entity.Card;
import lombok.Data;

import java.util.List;

@Data
public class GameStateDTO {
    private String gameId;
    private List<PlayerDTO> players;
    private List<Card> communityCards;
    private int pot;
    private String status;
}
