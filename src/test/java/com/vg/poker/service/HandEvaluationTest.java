package com.vg.poker.service;

import com.vg.poker.entity.Card;
import com.vg.poker.entity.enums.HandRank;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HandEvaluationTest {

    private Card card(String rank, String suit) {
        return new Card(rank, suit);
    }

    @Test
    void testPairBeatsHighCard() {

        List<Card> pairHand = List.of(
                card("A","SPADES"),
                card("A","HEARTS"),
                card("10","CLUBS"),
                card("7","DIAMONDS"),
                card("4","SPADES")
        );

        List<Card> highCardHand = List.of(
                card("K","SPADES"),
                card("Q","HEARTS"),
                card("10","CLUBS"),
                card("7","DIAMONDS"),
                card("4","SPADES")
        );

        HandScore pairScore = HandEvaluation.evaluate(pairHand);
        HandScore highScore = HandEvaluation.evaluate(highCardHand);

        assertTrue(pairScore.compareTo(highScore) > 0);
    }

    @Test
    void testTwoPair() {

        List<Card> cards = List.of(
                card("K","SPADES"),
                card("K","HEARTS"),
                card("10","CLUBS"),
                card("10","DIAMONDS"),
                card("4","SPADES")
        );

        HandScore score = HandEvaluation.evaluate(cards);

        assertEquals(HandRank.TWO_PAIR, score.getHandRank());
    }

    @Test
    void testThreeOfAKind() {

        List<Card> cards = List.of(
                card("Q","SPADES"),
                card("Q","HEARTS"),
                card("Q","CLUBS"),
                card("7","DIAMONDS"),
                card("2","SPADES")
        );

        HandScore score = HandEvaluation.evaluate(cards);

        assertEquals(HandRank.THREE_OF_A_KIND, score.getHandRank());
    }

    @Test
    void testStraight() {

        List<Card> cards = List.of(
                card("6","SPADES"),
                card("5","HEARTS"),
                card("4","CLUBS"),
                card("3","DIAMONDS"),
                card("2","SPADES")
        );

        HandScore score = HandEvaluation.evaluate(cards);

        assertEquals(HandRank.STRAIGHT, score.getHandRank());
    }

    @Test
    void testFlush() {

        List<Card> cards = List.of(
                card("A","HEARTS"),
                card("10","HEARTS"),
                card("7","HEARTS"),
                card("4","HEARTS"),
                card("2","HEARTS")
        );

        HandScore score = HandEvaluation.evaluate(cards);

        assertEquals(HandRank.FLUSH, score.getHandRank());
    }

    @Test
    void testRoyalFlush() {
        List<Card> cards = List.of(
                card("A","HEARTS"),
                card("K","HEARTS"),
                card("Q","HEARTS"),
                card("J","HEARTS"),
                card("10","HEARTS"),
                card("2","CLUBS"),
                card("3","SPADES")
        );

        HandScore score = HandEvaluation.evaluate(cards);

        assertEquals(HandRank.ROYAL_FLUSH, score.getHandRank());
    }

    @Test
    void testStraightFlush() {
        List<Card> cards = List.of(
                card("9","SPADES"),
                card("8","SPADES"),
                card("7","SPADES"),
                card("6","SPADES"),
                card("5","SPADES"),
                card("A","HEARTS"),
                card("A","CLUBS")
        );

        HandScore score = HandEvaluation.evaluate(cards);

        assertEquals(HandRank.STRAIGHT_FLUSH, score.getHandRank());
    }

    @Test
    void testFullHouse() {
        List<Card> cards = List.of(
                card("Q","SPADES"),
                card("Q","HEARTS"),
                card("Q","CLUBS"),
                card("7","DIAMONDS"),
                card("7","SPADES"),
                card("2","CLUBS"),
                card("3","HEARTS")
        );

        HandScore score = HandEvaluation.evaluate(cards);

        assertEquals(HandRank.FULL_HOUSE, score.getHandRank());
        assertEquals(List.of(12, 7), score.getTieBreakers());
    }

    @Test
    void testWheelStraight() {
        List<Card> cards = List.of(
                card("A","SPADES"),
                card("5","HEARTS"),
                card("4","CLUBS"),
                card("3","DIAMONDS"),
                card("2","SPADES"),
                card("K","CLUBS"),
                card("9","HEARTS")
        );

        HandScore score = HandEvaluation.evaluate(cards);

        assertEquals(HandRank.STRAIGHT, score.getHandRank());
        assertEquals(List.of(5), score.getTieBreakers());
    }

    @Test
    void testTieBreakerComparison() {
        HandScore acePair = HandEvaluation.evaluate(List.of(
                card("A","SPADES"),
                card("A","HEARTS"),
                card("K","CLUBS"),
                card("7","DIAMONDS"),
                card("4","SPADES")
        ));
        HandScore kingPair = HandEvaluation.evaluate(List.of(
                card("K","SPADES"),
                card("K","HEARTS"),
                card("Q","CLUBS"),
                card("7","DIAMONDS"),
                card("4","SPADES")
        ));

        assertTrue(acePair.compareTo(kingPair) > 0);
    }

    @Test
    void testBestFiveOfSevenCards() {
        List<Card> cards = List.of(
                card("A","HEARTS"),
                card("K","HEARTS"),
                card("Q","HEARTS"),
                card("J","HEARTS"),
                card("10","HEARTS"),
                card("A","SPADES"),
                card("A","CLUBS")
        );

        HandScore score = HandEvaluation.evaluate(cards);

        assertEquals(HandRank.ROYAL_FLUSH, score.getHandRank());
    }

}
