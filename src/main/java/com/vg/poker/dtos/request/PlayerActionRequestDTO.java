package com.vg.poker.dtos.request;

import lombok.Data;

@Data
public class PlayerActionRequestDTO {
    private String playerId;
    private int chips;
}
