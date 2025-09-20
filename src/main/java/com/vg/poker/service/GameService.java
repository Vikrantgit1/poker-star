package com.vg.poker.service;

import com.vg.poker.entity.Deck;
import com.vg.poker.entity.Game;
import com.vg.poker.entity.Player;
import com.vg.poker.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
                .build();
        gameRepository.save(game);

        return game;
    }

    public Optional<Game> addPlayer(String id, Player player) {
        Optional<Game> gameOptional = gameRepository.findById(id);
        gameOptional.ifPresent(
                game -> {
                    game.getPlayers().add(player);
                    gameRepository.save(game);
                }
        );
        return gameOptional;
    }

    public Optional<Game> dealCards(String id) {
        Optional<Game> gameOptional = gameRepository.findById(id);
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

    public Optional<Game> getGameState(String id) {
        return gameRepository.findById(id);
    }
}
