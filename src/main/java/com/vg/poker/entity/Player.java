package com.vg.poker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private int chips=0;
    private List<Card> hand = new ArrayList<>();
    private boolean folded = false;
}
