package com.vg.poker.service;

import com.vg.poker.dtos.request.AddPlayerRequestDTO;
import com.vg.poker.dtos.request.PlayerActionRequestDTO;
import com.vg.poker.dtos.response.GameStateDTO;
import com.vg.poker.entity.Card;
import com.vg.poker.entity.Deck;
import com.vg.poker.entity.Game;
import com.vg.poker.entity.Player;
import com.vg.poker.entity.enums.GamePhase;
import com.vg.poker.entity.enums.HandRank;
import com.vg.poker.entity.enums.PlayerActionType;
import com.vg.poker.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void createGameInitializesWaitingState() {
        Game game = gameService.createGame();

        assertEquals(GamePhase.WAITING, game.getStatus());
        assertNotNull(game.getDeck());
        assertTrue(game.getPlayers().isEmpty());
        assertTrue(game.getCommunityCards().isEmpty());
        assertEquals(0, game.getPot());
        verify(gameRepository).save(game);
    }

    @Test
    void addPlayerRejectsDuplicatesAndNonWaitingGames() {
        Game game = gameWithPlayers(GamePhase.WAITING, player("p1", "Alice", 100), player("p2", "Bob", 100));
        when(gameRepository.findById("g1")).thenReturn(Optional.of(game));

        AddPlayerRequestDTO duplicate = new AddPlayerRequestDTO();
        duplicate.setId("p1");
        duplicate.setName("Alice 2");
        duplicate.setChips(100);

        assertThrows(ResponseStatusException.class, () -> gameService.addPlayer("g1", duplicate));

        game.setStatus(GamePhase.PRE_FLOP);
        AddPlayerRequestDTO latePlayer = new AddPlayerRequestDTO();
        latePlayer.setId("p3");
        latePlayer.setName("Carol");
        latePlayer.setChips(100);

        assertThrows(ResponseStatusException.class, () -> gameService.addPlayer("g1", latePlayer));
    }

    @Test
    void dealRequiresTwoPlayersAndMovesToPreFlop() {
        Game onePlayerGame = gameWithPlayers(GamePhase.WAITING, player("p1", "Alice", 100));
        when(gameRepository.findById("g1")).thenReturn(Optional.of(onePlayerGame));

        assertThrows(ResponseStatusException.class, () -> gameService.dealCards("g1"));

        Game game = gameWithPlayers(GamePhase.WAITING, player("p1", "Alice", 100), player("p2", "Bob", 100));
        when(gameRepository.findById("g2")).thenReturn(Optional.of(game));

        Optional<Game> result = gameService.dealCards("g2");

        assertTrue(result.isPresent());
        assertEquals(GamePhase.PRE_FLOP, game.getStatus());
        assertEquals("p1", game.getCurrentPlayerId());
        assertEquals(0, game.getCurrentBet());
        assertEquals(2, game.getPlayers().get(0).getHand().size());
        assertEquals(2, game.getPlayers().get(1).getHand().size());
        assertEquals(48, game.getDeck().remainingCards());
        verify(gameRepository).save(game);
    }

    @Test
    void revealCommunityCardsEnforcesSequenceAndAutoFinishesAfterRiver() {
        Game game = gameWithPlayers(GamePhase.PRE_FLOP,
                playerWithHand("p1", "Alice", 100, List.of(card("A", "SPADES"), card("A", "HEARTS"))),
                playerWithHand("p2", "Bob", 100, List.of(card("K", "SPADES"), card("K", "HEARTS"))));
        game.setDeck(deckWithCards(List.of(
                card("7", "CLUBS"),
                card("8", "CLUBS"),
                card("9", "CLUBS"),
                card("9", "DIAMONDS"),
                card("8", "HEARTS"),
                card("4", "SPADES"),
                card("3", "DIAMONDS"),
                card("2", "CLUBS")
        )));
        when(gameRepository.findById("g1")).thenReturn(Optional.of(game));

        assertThrows(ResponseStatusException.class, () -> gameService.revealCommunityCards("g1", 1));

        gameService.revealCommunityCards("g1", 3);
        assertEquals(GamePhase.FLOP, game.getStatus());
        assertEquals(3, game.getCommunityCards().size());

        gameService.revealCommunityCards("g1", 1);
        assertEquals(GamePhase.TURN, game.getStatus());
        assertEquals(4, game.getCommunityCards().size());

        game.setPot(25);
        gameService.revealCommunityCards("g1", 1);
        assertEquals(GamePhase.FINISHED, game.getStatus());
        assertEquals(List.of("p1"), game.getWinnerPlayerIds());
        assertEquals(HandRank.ONE_PAIR, game.getWinningHandRank());
        assertEquals(125, game.getPlayers().get(0).getChips());
        assertEquals(0, game.getPot());
    }

    @Test
    void betRejectsInvalidAmountsAndFoldedPlayers() {
        Game game = gameWithPlayers(GamePhase.PRE_FLOP, player("p1", "Alice", 100), player("p2", "Bob", 100));
        game.getPlayers().get(1).setFolded(true);
        game.setCurrentPlayerId("p1");
        when(gameRepository.findById("g1")).thenReturn(Optional.of(game));

        PlayerActionRequestDTO zero = action("p1", 0);
        assertThrows(ResponseStatusException.class, () -> gameService.playerBet("g1", zero));

        PlayerActionRequestDTO tooMuch = action("p1", 101);
        assertThrows(ResponseStatusException.class, () -> gameService.playerBet("g1", tooMuch));

        PlayerActionRequestDTO folded = action("p2", 10);
        assertThrows(ResponseStatusException.class, () -> gameService.playerBet("g1", folded));

        game.getPlayers().get(1).setFolded(false);
        game.setCurrentPlayerId("p1");
        PlayerActionRequestDTO valid = action("p1", 30);
        gameService.playerBet("g1", valid);

        assertEquals(70, game.getPlayers().get(0).getChips());
        assertEquals(30, game.getPot());
        assertEquals("p2", game.getCurrentPlayerId());
    }

    @Test
    void foldAwardsPotWhenOnlyOneActivePlayerRemains() {
        Game game = gameWithPlayers(GamePhase.PRE_FLOP, player("p1", "Alice", 100), player("p2", "Bob", 100));
        game.setPot(20);
        game.setCurrentPlayerId("p2");
        when(gameRepository.findById("g1")).thenReturn(Optional.of(game));

        gameService.playerFold("g1", "p2");

        assertEquals(GamePhase.FINISHED, game.getStatus());
        assertEquals(120, game.getPlayers().get(0).getChips());
        assertEquals(0, game.getPot());
        assertEquals(List.of("p1"), game.getWinnerPlayerIds());
    }

    @Test
    void actionRequiresCurrentPlayerAndAdvancesRoundAfterChecks() {
        Game game = gameWithPlayers(GamePhase.PRE_FLOP, player("p1", "Alice", 100), player("p2", "Bob", 100));
        game.setCurrentPlayerId("p1");
        when(gameRepository.findById("g1")).thenReturn(Optional.of(game));

        assertThrows(ResponseStatusException.class, () -> gameService.playerAction("g1", action("p2", PlayerActionType.CHECK, 0)));

        gameService.playerAction("g1", action("p1", PlayerActionType.CHECK, 0));

        assertEquals(GamePhase.PRE_FLOP, game.getStatus());
        assertEquals("p2", game.getCurrentPlayerId());

        gameService.playerAction("g1", action("p2", PlayerActionType.CHECK, 0));

        assertEquals(GamePhase.FLOP, game.getStatus());
        assertEquals(3, game.getCommunityCards().size());
        assertEquals(0, game.getCurrentBet());
        assertEquals("p1", game.getCurrentPlayerId());
        assertFalse(game.getPlayers().get(0).isActedThisRound());
        assertFalse(game.getPlayers().get(1).isActedThisRound());
    }

    @Test
    void actionSupportsCallAndRaiseState() {
        Game game = gameWithPlayers(GamePhase.PRE_FLOP, player("p1", "Alice", 100), player("p2", "Bob", 100));
        game.setCurrentPlayerId("p1");
        when(gameRepository.findById("g1")).thenReturn(Optional.of(game));

        gameService.playerAction("g1", action("p1", PlayerActionType.BET, 10));

        assertEquals(10, game.getCurrentBet());
        assertEquals(10, game.getMinRaise());
        assertEquals("p2", game.getCurrentPlayerId());
        assertEquals(90, game.getPlayers().get(0).getChips());
        assertEquals(10, game.getPot());

        gameService.playerAction("g1", action("p2", PlayerActionType.CALL, 0));

        assertEquals(GamePhase.FLOP, game.getStatus());
        assertEquals(20, game.getPot());
        assertEquals(90, game.getPlayers().get(1).getChips());
        assertEquals(0, game.getCurrentBet());
    }

    @Test
    void showdownSplitsTiedPotDeterministically() {
        Game game = gameWithPlayers(GamePhase.TURN,
                playerWithHand("p1", "Alice", 100, List.of(card("2", "SPADES"), card("3", "CLUBS"))),
                playerWithHand("p2", "Bob", 100, List.of(card("4", "SPADES"), card("5", "CLUBS"))));
        game.setCommunityCards(new ArrayList<>(List.of(
                card("10", "HEARTS"),
                card("J", "HEARTS"),
                card("Q", "HEARTS"),
                card("K", "HEARTS")
        )));
        game.setDeck(deckWithCards(List.of(card("7", "CLUBS"), card("A", "HEARTS"))));
        game.setPot(21);
        when(gameRepository.findById("g1")).thenReturn(Optional.of(game));

        gameService.revealCommunityCards("g1", 1);

        assertEquals(GamePhase.FINISHED, game.getStatus());
        assertEquals(List.of("p1", "p2"), game.getWinnerPlayerIds());
        assertEquals(HandRank.ROYAL_FLUSH, game.getWinningHandRank());
        assertEquals(111, game.getPlayers().get(0).getChips());
        assertEquals(110, game.getPlayers().get(1).getChips());
        assertEquals(0, game.getPot());
    }

    @Test
    void dtoHidesOpponentCardsBeforeFinishAndRevealsAfterFinish() {
        Game game = gameWithPlayers(GamePhase.PRE_FLOP,
                playerWithHand("p1", "Alice", 100, List.of(card("A", "SPADES"), card("A", "HEARTS"))),
                playerWithHand("p2", "Bob", 100, List.of(card("K", "SPADES"), card("K", "HEARTS"))));

        GameStateDTO beforeFinish = gameService.mapGameToDTO(game, "p1");

        assertNotNull(beforeFinish.getPlayers().get(0).getHand());
        assertNull(beforeFinish.getPlayers().get(1).getHand());
        assertTrue(beforeFinish.getLegalActions().isEmpty());

        game.setStatus(GamePhase.FINISHED);
        GameStateDTO afterFinish = gameService.mapGameToDTO(game, "p1");

        assertNotNull(afterFinish.getPlayers().get(0).getHand());
        assertNotNull(afterFinish.getPlayers().get(1).getHand());
    }

    private Game gameWithPlayers(GamePhase status, Player... players) {
        return Game.builder()
                .id("g1")
                .autoStartNextRound(false)
                .status(status)
                .players(new ArrayList<>(List.of(players)))
                .deck(new Deck())
                .communityCards(new ArrayList<>())
                .build();
    }

    private Player player(String id, String name, int chips) {
        return Player.builder()
                .id(id)
                .name(name)
                .chips(chips)
                .build();
    }

    private Player playerWithHand(String id, String name, int chips, List<Card> hand) {
        Player player = player(id, name, chips);
        player.setHand(new ArrayList<>(hand));
        return player;
    }

    private Card card(String rank, String suit) {
        return new Card(rank, suit);
    }

    private Deck deckWithCards(List<Card> cards) {
        return new Deck(new ArrayList<>(cards));
    }

    private PlayerActionRequestDTO action(String playerId, int chips) {
        PlayerActionRequestDTO request = new PlayerActionRequestDTO();
        request.setPlayerId(playerId);
        request.setChips(chips);
        return request;
    }

    private PlayerActionRequestDTO action(String playerId, PlayerActionType action, int amount) {
        PlayerActionRequestDTO request = new PlayerActionRequestDTO();
        request.setPlayerId(playerId);
        request.setAction(action);
        request.setAmount(amount);
        return request;
    }
}
