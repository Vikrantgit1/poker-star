package com.vg.poker.service;

<<<<<<< Updated upstream
=======
import com.vg.poker.dtos.request.AddPlayerRequestDTO;
import com.vg.poker.dtos.request.PlayerActionRequestDTO;
import com.vg.poker.dtos.response.GameStateDTO;
import com.vg.poker.dtos.response.PlayerDTO;
import com.vg.poker.entity.Card;
>>>>>>> Stashed changes
import com.vg.poker.entity.Deck;
import com.vg.poker.entity.Game;
import com.vg.poker.entity.Player;
import com.vg.poker.entity.enums.GamePhase;
import com.vg.poker.entity.enums.HandRank;
import com.vg.poker.entity.enums.PlayerActionType;
import com.vg.poker.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
<<<<<<< Updated upstream
=======
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
>>>>>>> Stashed changes
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    
    public Game createGame() {
        Game game = Game.builder()
                .deck(new Deck())
                .communityCards(new ArrayList<>())
                .players(new ArrayList<>())
                .pot(0)
                .status(GamePhase.WAITING)
                .currentBet(0)
                .minRaise(1)
                .winnerPlayerIds(new ArrayList<>())
                .winnerNames(new ArrayList<>())
                .build();
        gameRepository.save(game);

        return game;
    }

    public Optional<Game> addPlayer(String id, Player player) {
        Optional<Game> gameOptional = gameRepository.findById(id);
        gameOptional.ifPresent(
                game -> {
<<<<<<< Updated upstream
                    game.getPlayers().add(player);
=======
                    requirePhase(game, GamePhase.WAITING, "Players can only join a waiting game");
                    validatePlayerRequest(playerRequest);
                    if (game.getPlayers().stream().anyMatch(player -> player.getId().equals(playerRequest.getId()))) {
                        throw badRequest("Player already joined this game");
                    }
                    game.getPlayers().add(
                            Player.builder()
                                    .id(playerRequest.getId())
                                    .name(playerRequest.getName())
                                    .chips(playerRequest.getChips())
                                    .build()
                    );
>>>>>>> Stashed changes
                    gameRepository.save(game);
                }
        );
        return gameOptional;
    }

    public Optional<Game> dealCards(String id) {
        Optional<Game> gameOptional = gameRepository.findById(id);
        gameOptional.ifPresent(
                game -> {
                    requirePhase(game, GamePhase.WAITING, "Cards can only be dealt from WAITING");
                    if (game.getPlayers().size() < 2) {
                        throw badRequest("At least two players are required to deal cards");
                    }
                    Deck deck = game.getDeck();
                    if (deck == null || deck.remainingCards() < game.getPlayers().size() * 2) {
                        deck = new Deck();
                    }
                    game.setCommunityCards(new ArrayList<>());
                    game.setPot(0);
                    clearWinners(game);
                    game.setWinningHandRank(null);
                    for (Player player : game.getPlayers()) {
                        player.setHand(new ArrayList<>());
                        player.setFolded(false);
                        player.setCurrentRoundBet(0);
                        player.setActedThisRound(false);
                    }
                    for(int i=0;i<2;++i){
                        for(Player player:game.getPlayers()){
                            player.getHand().add(deck.draw());
                        }
                    }
                    game.setStatus(GamePhase.PRE_FLOP);
                    resetBettingRound(game);
                    game.setDeck(deck);
                    gameRepository.save(game);
                }
        );
        return gameOptional;
    }

    public Optional<Game> getGameState(String id) {
        return gameRepository.findById(id);
    }
<<<<<<< Updated upstream
=======

    public Optional<Game> playerBet(String gameId, PlayerActionRequestDTO request) {
        if (request != null && request.getAction() == null) {
            request.setAction(PlayerActionType.BET);
        }
        return playerAction(gameId, request);
    }

    public Optional<Game> playerAction(String gameId, PlayerActionRequestDTO request) {
        Optional<Game> optGame = gameRepository.findById(gameId);
        optGame.ifPresent(game -> {
            requireActiveGame(game);
            if (request == null || !StringUtils.hasText(request.getPlayerId())) {
                throw badRequest("Player id is required");
            }
            if (request.getAction() == null) {
                throw badRequest("Player action is required");
            }
            Player player = findPlayerOrThrow(game, request.getPlayerId());
            requireCurrentTurn(game, player);
            if (player.isFolded()) {
                throw badRequest("Folded players cannot act");
            }
            applyPlayerAction(game, player, request);
            finishAction(game);
            gameRepository.save(game);
        });
        return optGame;
    }

    public Optional<Game> playerFold(String gameId, String playerId) {
        Optional<Game> optGame = gameRepository.findById(gameId);
        optGame.ifPresent(game -> {
            requireActiveGame(game);
            if (!StringUtils.hasText(playerId)) {
                throw badRequest("Player id is required");
            }
            Player player = findPlayerOrThrow(game, playerId);
            requireCurrentTurn(game, player);
            if (player.isFolded()) {
                throw badRequest("Player is already folded");
            }
            applyFold(game, player);
            finishAction(game);
            gameRepository.save(game);
        });
        return optGame;
    }

    // Reveal community cards (flop/turn/river)
    public Optional<Game> revealCommunityCards(String gameId, int count) {
        Optional<Game> optGame = gameRepository.findById(gameId);
        optGame.ifPresent(game -> {
            requireActiveGame(game);
            Deck deck = game.getDeck();
            if (deck == null) {
                throw badRequest("Game deck is missing");
            }
            GamePhase nextPhase = switch (game.getStatus()) {
                case PRE_FLOP -> {
                    if (count != 3) throw badRequest("Flop reveal requires exactly 3 cards");
                    yield GamePhase.FLOP;
                }
                case FLOP -> {
                    if (count != 1) throw badRequest("Turn reveal requires exactly 1 card");
                    yield GamePhase.TURN;
                }
                case TURN -> {
                    if (count != 1) throw badRequest("River reveal requires exactly 1 card");
                    yield GamePhase.RIVER;
                }
                default -> throw badRequest("Community cards cannot be revealed in " + game.getStatus());
            };
            if (deck.remainingCards() < count + 1) {
                throw badRequest("Not enough cards remain in the deck");
            }
            deck.burn();
            for (int i = 0; i < count; i++) {
                game.getCommunityCards().add(deck.draw());
            }
            game.setDeck(deck);
            game.setStatus(nextPhase);
            if (nextPhase == GamePhase.RIVER) {
                settleShowdown(game);
            } else {
                resetBettingRound(game);
            }
            gameRepository.save(game);
        });
        return optGame;
    }

    public GameStateDTO mapGameToDTO(Game game) {
        return mapGameToDTO(game, null);
    }

    public GameStateDTO mapGameToDTO(Game game, String viewerPlayerId) {
        GameStateDTO dto = new GameStateDTO();
        dto.setGameId(game.getId());
        dto.setPot(game.getPot());
        dto.setStatus(game.getStatus());
        dto.setCurrentPlayerId(game.getCurrentPlayerId());
        dto.setCurrentBet(game.getCurrentBet());
        dto.setMinRaise(game.getMinRaise());
        dto.setLegalActions(legalActionsForViewer(game, viewerPlayerId));
        dto.setCommunityCards(game.getCommunityCards());
        dto.setWinnerPlayerIds(game.getWinnerPlayerIds());
        dto.setWinnerNames(game.getWinnerNames());
        dto.setWinningHandRank(game.getWinningHandRank());

        List<PlayerDTO> players = game.getPlayers().stream().map(player -> {
            PlayerDTO pDto = new PlayerDTO();
            pDto.setPlayerId(player.getId());
            pDto.setName(player.getName());
            pDto.setChips(player.getChips());
            pDto.setFolded(player.isFolded());
            pDto.setCurrentRoundBet(player.getCurrentRoundBet());
            pDto.setActedThisRound(player.isActedThisRound());
            pDto.setHand(shouldRevealHand(game, player, viewerPlayerId) ? player.getHand() : null);
            return pDto;
        }).collect(Collectors.toList());

        dto.setPlayers(players);
        return dto;
    }

    private void validatePlayerRequest(AddPlayerRequestDTO playerRequest) {
        if (playerRequest == null) {
            throw badRequest("Player request is required");
        }
        if (!StringUtils.hasText(playerRequest.getId())) {
            throw badRequest("Player id is required");
        }
        if (!StringUtils.hasText(playerRequest.getName())) {
            throw badRequest("Player name is required");
        }
        if (playerRequest.getChips() < 0) {
            throw badRequest("Player chips cannot be negative");
        }
    }

    private Player findPlayerOrThrow(Game game, String playerId) {
        return game.getPlayers().stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> badRequest("Player is not part of this game"));
    }

    private List<Player> activePlayers(Game game) {
        return game.getPlayers().stream()
                .filter(player -> !player.isFolded())
                .collect(Collectors.toList());
    }

    private void requireCurrentTurn(Game game, Player player) {
        if (game.getCurrentPlayerId() == null) {
            game.setCurrentPlayerId(firstActivePlayerId(game));
        }
        if (!Objects.equals(game.getCurrentPlayerId(), player.getId())) {
            throw badRequest("It is not this player's turn");
        }
    }

    private void applyPlayerAction(Game game, Player player, PlayerActionRequestDTO request) {
        switch (request.getAction()) {
            case CHECK -> applyCheck(game, player);
            case CALL -> applyCall(game, player);
            case BET -> applyBet(game, player, request.requestedAmount());
            case RAISE -> applyRaise(game, player, request.requestedAmount());
            case FOLD -> applyFold(game, player);
        }
    }

    private void applyCheck(Game game, Player player) {
        if (player.getCurrentRoundBet() != game.getCurrentBet()) {
            throw badRequest("Player cannot check while facing a bet");
        }
        player.setActedThisRound(true);
    }

    private void applyCall(Game game, Player player) {
        int callAmount = game.getCurrentBet() - player.getCurrentRoundBet();
        if (callAmount <= 0) {
            throw badRequest("Player has no bet to call");
        }
        collectChips(game, player, callAmount);
        player.setActedThisRound(true);
    }

    private void applyBet(Game game, Player player, int amount) {
        if (game.getCurrentBet() != 0) {
            throw badRequest("Use RAISE when a bet already exists");
        }
        if (amount < game.getMinRaise()) {
            throw badRequest("Bet must be at least the minimum raise");
        }
        collectChips(game, player, amount);
        game.setCurrentBet(player.getCurrentRoundBet());
        game.setMinRaise(amount);
        markOthersUnacted(game, player);
        player.setActedThisRound(true);
    }

    private void applyRaise(Game game, Player player, int amount) {
        if (game.getCurrentBet() == 0) {
            throw badRequest("Use BET when no bet exists");
        }
        int newRoundBet = player.getCurrentRoundBet() + amount;
        int raiseBy = newRoundBet - game.getCurrentBet();
        if (raiseBy < game.getMinRaise()) {
            throw badRequest("Raise must be at least the minimum raise");
        }
        collectChips(game, player, amount);
        game.setCurrentBet(player.getCurrentRoundBet());
        game.setMinRaise(raiseBy);
        markOthersUnacted(game, player);
        player.setActedThisRound(true);
    }

    private void applyFold(Game game, Player player) {
        player.setFolded(true);
        player.setActedThisRound(true);
    }

    private void collectChips(Game game, Player player, int amount) {
        if (amount <= 0) {
            throw badRequest("Action amount must be greater than zero");
        }
        if (player.getChips() < amount) {
            throw badRequest("Player does not have enough chips");
        }
        player.setChips(player.getChips() - amount);
        player.setCurrentRoundBet(player.getCurrentRoundBet() + amount);
        game.setPot(game.getPot() + amount);
    }

    private void finishAction(Game game) {
        List<Player> activePlayers = activePlayers(game);
        if (activePlayers.size() == 1) {
            awardPotToSingleWinner(game, activePlayers.get(0));
            game.setCurrentPlayerId(null);
            return;
        }
        if (isBettingRoundComplete(game, activePlayers)) {
            advanceAfterBettingRound(game);
            return;
        }
        game.setCurrentPlayerId(nextActionPlayer(game));
    }

    private boolean isBettingRoundComplete(Game game, List<Player> activePlayers) {
        return activePlayers.stream()
                .allMatch(player -> player.isActedThisRound() && player.getCurrentRoundBet() == game.getCurrentBet());
    }

    private void advanceAfterBettingRound(Game game) {
        switch (game.getStatus()) {
            case PRE_FLOP -> revealStreet(game, 3, GamePhase.FLOP);
            case FLOP -> revealStreet(game, 1, GamePhase.TURN);
            case TURN -> revealStreet(game, 1, GamePhase.RIVER);
            case RIVER -> settleShowdown(game);
            default -> throw badRequest("Game is not active");
        }
    }

    private void revealStreet(Game game, int count, GamePhase nextPhase) {
        Deck deck = game.getDeck();
        if (deck == null) {
            throw badRequest("Game deck is missing");
        }
        if (deck.remainingCards() < count + 1) {
            throw badRequest("Not enough cards remain in the deck");
        }
        deck.burn();
        for (int i = 0; i < count; i++) {
            game.getCommunityCards().add(deck.draw());
        }
        game.setDeck(deck);
        game.setStatus(nextPhase);
        resetBettingRound(game);
    }

    private void resetBettingRound(Game game) {
        game.setCurrentBet(0);
        game.setMinRaise(1);
        for (Player player : game.getPlayers()) {
            player.setCurrentRoundBet(0);
            player.setActedThisRound(false);
        }
        game.setCurrentPlayerId(firstActivePlayerId(game));
    }

    private String firstActivePlayerId(Game game) {
        return activePlayers(game).stream()
                .map(Player::getId)
                .findFirst()
                .orElse(null);
    }

    private String nextActionPlayer(Game game) {
        List<Player> players = game.getPlayers();
        if (players.isEmpty()) {
            return null;
        }
        int start = indexOfPlayer(game, game.getCurrentPlayerId());
        for (int i = 1; i <= players.size(); i++) {
            Player candidate = players.get((start + i) % players.size());
            if (!candidate.isFolded()
                    && (!candidate.isActedThisRound() || candidate.getCurrentRoundBet() < game.getCurrentBet())) {
                return candidate.getId();
            }
        }
        return null;
    }

    private int indexOfPlayer(Game game, String playerId) {
        for (int i = 0; i < game.getPlayers().size(); i++) {
            if (Objects.equals(game.getPlayers().get(i).getId(), playerId)) {
                return i;
            }
        }
        return -1;
    }

    private void markOthersUnacted(Game game, Player actingPlayer) {
        for (Player player : activePlayers(game)) {
            if (!Objects.equals(player.getId(), actingPlayer.getId())) {
                player.setActedThisRound(false);
            }
        }
    }

    private void requirePhase(Game game, GamePhase phase, String message) {
        if (game.getStatus() != phase) {
            throw badRequest(message);
        }
    }

    private void requireActiveGame(Game game) {
        if (game.getStatus() != GamePhase.PRE_FLOP
                && game.getStatus() != GamePhase.FLOP
                && game.getStatus() != GamePhase.TURN
                && game.getStatus() != GamePhase.RIVER) {
            throw badRequest("Game is not active");
        }
    }

    private void settleShowdown(Game game) {
        List<Player> activePlayers = activePlayers(game);
        if (activePlayers.isEmpty()) {
            clearWinners(game);
            game.setWinningHandRank(null);
            game.setPot(0);
            game.setStatus(GamePhase.FINISHED);
            game.setCurrentPlayerId(null);
            return;
        }

        HandScore bestScore = activePlayers.stream()
                .map(player -> HandEvaluation.evaluate(cardsForShowdown(player, game)))
                .max(Comparator.naturalOrder())
                .orElseThrow();

        List<Player> winners = activePlayers.stream()
                .filter(player -> HandEvaluation.evaluate(cardsForShowdown(player, game)).compareTo(bestScore) == 0)
                .collect(Collectors.toList());

        applyWinners(game, winners, bestScore.getHandRank());
        game.setStatus(GamePhase.FINISHED);
        game.setCurrentPlayerId(null);
    }

    private List<Card> cardsForShowdown(Player player, Game game) {
        List<Card> cards = new ArrayList<>();
        if (player.getHand() != null) {
            cards.addAll(player.getHand());
        }
        if (game.getCommunityCards() != null) {
            cards.addAll(game.getCommunityCards());
        }
        return cards;
    }

    private void awardPotToSingleWinner(Game game, Player winner) {
        applyWinners(game, List.of(winner), null);
        game.setStatus(GamePhase.FINISHED);
    }

    private void applyWinners(Game game, List<Player> winners, HandRank winningHandRank) {
        clearWinners(game);
        if (winners.isEmpty()) {
            game.setPot(0);
            game.setWinningHandRank(null);
            return;
        }

        int split = game.getPot() / winners.size();
        int remainder = game.getPot() % winners.size();
        for (int i = 0; i < winners.size(); i++) {
            Player winner = winners.get(i);
            winner.setChips(winner.getChips() + split + (i == 0 ? remainder : 0));
            game.getWinnerPlayerIds().add(winner.getId());
            game.getWinnerNames().add(winner.getName());
        }
        game.setPot(0);
        game.setWinningHandRank(winningHandRank);
    }

    private void clearWinners(Game game) {
        game.setWinnerPlayerIds(new ArrayList<>());
        game.setWinnerNames(new ArrayList<>());
    }

    private boolean shouldRevealHand(Game game, Player player, String viewerPlayerId) {
        return game.getStatus() == GamePhase.FINISHED || Objects.equals(player.getId(), viewerPlayerId);
    }

    private List<PlayerActionType> legalActionsForViewer(Game game, String viewerPlayerId) {
        if (!isActivePhase(game.getStatus()) || !Objects.equals(game.getCurrentPlayerId(), viewerPlayerId)) {
            return List.of();
        }
        Player player = findPlayerOrThrow(game, viewerPlayerId);
        if (player.isFolded()) {
            return List.of();
        }
        List<PlayerActionType> actions = new ArrayList<>();
        if (player.getCurrentRoundBet() == game.getCurrentBet()) {
            actions.add(PlayerActionType.CHECK);
            if (game.getCurrentBet() == 0) {
                actions.add(PlayerActionType.BET);
            }
        } else {
            actions.add(PlayerActionType.CALL);
            actions.add(PlayerActionType.RAISE);
        }
        actions.add(PlayerActionType.FOLD);
        return actions;
    }

    private boolean isActivePhase(GamePhase phase) {
        return phase == GamePhase.PRE_FLOP
                || phase == GamePhase.FLOP
                || phase == GamePhase.TURN
                || phase == GamePhase.RIVER;
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

>>>>>>> Stashed changes
}
