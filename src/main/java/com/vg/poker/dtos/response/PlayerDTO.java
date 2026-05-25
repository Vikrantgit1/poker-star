package com.vg.poker.dtos.response;

import com.vg.poker.entity.Card;
import lombok.Data;

import java.util.List;

@Data
public class PlayerDTO {
    private String name;
    private int chips;
    private List<Card> hand;
    private boolean folded;
}
