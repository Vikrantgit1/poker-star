package com.vg.poker.controller;

import com.vg.poker.entity.Game;
import com.vg.poker.entity.Player;
import com.vg.poker.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @PostMapping("/create")
    public ResponseEntity<Game> createGame(){
        Game game = gameService.createGame();
        return ResponseEntity.ok(game);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Game> joinGame(@PathVariable String id, @RequestBody Player player) {
        Optional<Game> game = gameService.addPlayer(id, player);
        return game.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/deal")
    public ResponseEntity<Game> dealCards(@PathVariable String id) {
        Optional<Game> game = gameService.dealCards(id);
        return game.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/state")
    public ResponseEntity<Game> getGameState(@PathVariable String id) {
        Optional<Game> game = gameService.getGameState(id);
        return game.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
