package com.vg.poker.entity;

import com.vg.poker.entity.enums.GamePhase;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DomainDefaultsTest {

    @Test
    void deckCreates52CardsWithRankThenSuitValues() {
        Deck deck = new Deck();

        assertEquals(52, deck.remainingCards());

        Set<String> ranks = deck.getCards().stream()
                .map(Card::getRank)
                .collect(Collectors.toSet());
        Set<String> suits = deck.getCards().stream()
                .map(Card::getSuit)
                .collect(Collectors.toSet());

        assertEquals(Set.of("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"), ranks);
        assertEquals(Set.of("HEARTS", "SPADES", "DIAMONDS", "CLUBS"), suits);
    }

    @Test
    void builderCreatedPlayerHasMutableDefaults() {
        Player player = Player.builder()
                .id("p1")
                .name("Alice")
                .build();

        assertNotNull(player.getHand());
        assertTrue(player.getHand().isEmpty());
        assertFalse(player.isFolded());
        assertEquals(0, player.getChips());
    }

    @Test
    void builderCreatedGameHasMutableDefaults() {
        Game game = Game.builder().build();

        assertNotNull(game.getPlayers());
        assertNotNull(game.getCommunityCards());
        assertNotNull(game.getDeck());
        assertEquals(0, game.getPot());
        assertEquals(GamePhase.WAITING, game.getStatus());
        assertNull(game.getCurrentPlayerId());
        assertEquals(0, game.getCurrentBet());
        assertEquals(1, game.getMinRaise());
        assertNotNull(game.getWinnerPlayerIds());
        assertNotNull(game.getWinnerNames());
    }
}
