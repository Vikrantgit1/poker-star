package com.vg.poker.dtos.request;

import lombok.Data;

@Data
public class AddPlayerRequestDTO {
    private String id;
    private String name;
    private int chips;
}
