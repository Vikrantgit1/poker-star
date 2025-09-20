package com.vg.poker.service;

import com.vg.poker.dtos.request.AddPlayerRequestDTO;
import com.vg.poker.dtos.request.PlayerActionRequestDTO;
import com.vg.poker.dtos.response.GameStateDTO;
import com.vg.poker.dtos.response.PlayerDTO;
import com.vg.poker.entity.Deck;
import com.vg.poker.entity.Game;
import com.vg.poker.entity.Player;
import com.vg.poker.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    
    public Game createGame() {
        Game game = Game.builder()
                .deck(new Deck())
                .communityCards(new ArrayList<>())
                .players(new ArrayList<>())
                .build();
        gameRepository.save(game);

        return game;
    }

    public Optional<Game> addPlayer(String gameId, AddPlayerRequestDTO playerRequest) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        gameOptional.ifPresent(
                game -> {
                    game.getPlayers().add(
                            Player.builder()
                                    .id(playerRequest.getId())
                                    .name(playerRequest.getName())
                                    .chips(playerRequest.getChips())
                                    .build()
                    );
                    gameRepository.save(game);
                }
        );
        return gameOptional;
    }

    public Optional<Game> dealCards(String gameId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        gameOptional.ifPresent(
                game -> {
                    Deck deck = game.getDeck();
                    for(int i=0;i<2;++i){
                        for(Player player:game.getPlayers()){
                            player.getHand().add(deck.draw());
                        }
                    }
                    game.setStatus("IN_PROGRESS");
                    game.setDeck(deck);
                    gameRepository.save(game);
                }
        );
        return gameOptional;
    }

    public Optional<Game> getGameState(String gameId) {
        return gameRepository.findById(gameId);
    }

    public Optional<Game> playerBet(String gameId, PlayerActionRequestDTO request) {
        Optional<Game> optGame = gameRepository.findById(gameId);
        optGame.ifPresent(game -> {
            for (Player player : game.getPlayers()) {
                if (player.getId().equals(request.getPlayerId()) && !player.isFolded()) {
                    int chips = request.getChips();
                    if (player.getChips() >= chips) {
                        player.setChips(player.getChips() - chips);
                        game.setPot(game.getPot() + chips);
                    }
                }
            }
            gameRepository.save(game);
        });
        return optGame;
    }

    public Optional<Game> playerFold(String gameId, String playerId) {
        Optional<Game> optGame = gameRepository.findById(gameId);
        optGame.ifPresent(game -> {
            for (Player player : game.getPlayers()) {
                if (player.getId().equals(playerId)) {
                    player.setFolded(true);
                }
            }
            gameRepository.save(game);
        });
        return optGame;
    }

    // Reveal community cards (flop/turn/river)
    public Optional<Game> revealCommunityCards(String gameId, int count) {
        Optional<Game> optGame = gameRepository.findById(gameId);
        optGame.ifPresent(game -> {
            Deck deck = game.getDeck();
            for (int i = 0; i < count; i++) {
                game.getCommunityCards().add(deck.draw());
            }
            game.setDeck(deck);
            gameRepository.save(game);
        });
        return optGame;
    }

    public GameStateDTO mapGameToDTO(Game game) {
        GameStateDTO dto = new GameStateDTO();
        dto.setGameId(game.getId());
        dto.setPot(game.getPot());
        dto.setStatus(game.getStatus());
        dto.setCommunityCards(game.getCommunityCards());

        List<PlayerDTO> players = game.getPlayers().stream().map(player -> {
            PlayerDTO pDto = new PlayerDTO();
            pDto.setName(player.getName());
            pDto.setChips(player.getChips());
            pDto.setFolded(player.isFolded());
            pDto.setHand(player.getHand()); // optional: hide until showdown
            return pDto;
        }).collect(Collectors.toList());

        dto.setPlayers(players);
        return dto;
    }

}
