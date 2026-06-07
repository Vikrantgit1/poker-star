package com.vg.poker.dtos.request;

import com.vg.poker.entity.enums.PlayerActionType;
import lombok.Data;

@Data
public class PlayerActionRequestDTO {
    private String playerId;
    private PlayerActionType action;
    private Integer amount;
    private int chips;

    public int requestedAmount() {
        return amount != null ? amount : chips;
    }
}
