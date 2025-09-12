package com.vg.poker.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "players")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    @Id
    private String id;
    private String name;
    private int chips=0;
}
