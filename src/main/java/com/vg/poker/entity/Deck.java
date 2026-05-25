package com.vg.poker.entity;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;

@Data
@AllArgsConstructor
public class Deck {
    private List<Card> cards = new ArrayList<>();

    public Deck(){
        String[] suits = {"HEARTS", "SPADES", "DIAMONDS", "CLUBS"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        for(String suit:suits){
            for(String rank:ranks){
                cards.add(new Card(rank, suit));
            }
        }
        shuffle(cards);
    }

    public Card draw(){
        if (cards.isEmpty()) {
            throw new RuntimeException("Deck is empty");
        }
        return cards.remove(cards.size()-1);
    }

    public void burn() {
        if (cards.isEmpty()) {
            throw new RuntimeException("Deck is empty");
        }
        cards.remove(0);
    }

    public int remainingCards() {
        return cards.size();
    }
}
