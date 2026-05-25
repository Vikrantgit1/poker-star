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
<<<<<<< Updated upstream
    public ResponseEntity<Game> createGame(){
        Game game = gameService.createGame();
        return ResponseEntity.ok(game);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Game> joinGame(@PathVariable String id, @RequestBody Player player) {
        Optional<Game> game = gameService.addPlayer(id, player);
        return game.map(ResponseEntity::ok)
=======
    public ResponseEntity<GameStateDTO> createGame(@RequestParam(required = false) String viewerPlayerId){
        Game game = gameService.createGame();
        return ResponseEntity.ok(gameService.mapGameToDTO(game, viewerPlayerId));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<GameStateDTO> joinGame(@PathVariable String id,
                                                 @RequestBody AddPlayerRequestDTO playerRequest,
                                                 @RequestParam(required = false) String viewerPlayerId) {
        Optional<Game> game = gameService.addPlayer(id, playerRequest);
        return game.map(value -> gameService.mapGameToDTO(value, viewerPlayerId))
                .map(ResponseEntity::ok)
>>>>>>> Stashed changes
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/deal")
<<<<<<< Updated upstream
    public ResponseEntity<Game> dealCards(@PathVariable String id) {
        Optional<Game> game = gameService.dealCards(id);
        return game.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/state")
    public ResponseEntity<Game> getGameState(@PathVariable String id) {
        Optional<Game> game = gameService.getGameState(id);
        return game.map(ResponseEntity::ok)
=======
    public ResponseEntity<GameStateDTO> dealCards(@PathVariable String id,
                                                  @RequestParam(required = false) String viewerPlayerId) {
        Optional<Game> game = gameService.dealCards(id);
        return game.map(value -> gameService.mapGameToDTO(value, viewerPlayerId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Player bets
    @PostMapping("/{id}/bet")
    public ResponseEntity<GameStateDTO> bet(@PathVariable String id,
                                            @RequestBody PlayerActionRequestDTO request,
                                            @RequestParam(required = false) String viewerPlayerId) {
        Optional<Game> game = gameService.playerBet(id, request);
        return game.map(value -> gameService.mapGameToDTO(value, viewerPlayerId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/action")
    public ResponseEntity<GameStateDTO> action(@PathVariable String id,
                                               @RequestBody PlayerActionRequestDTO request,
                                               @RequestParam(required = false) String viewerPlayerId) {
        Optional<Game> game = gameService.playerAction(id, request);
        return game.map(value -> gameService.mapGameToDTO(value, viewerPlayerId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Player folds
    @PostMapping("/{id}/fold")
    public ResponseEntity<GameStateDTO> fold(@PathVariable String id,
                                             @RequestBody PlayerActionRequestDTO request,
                                             @RequestParam(required = false) String viewerPlayerId) {
        Optional<Game> game = gameService.playerFold(id, request != null ? request.getPlayerId() : null);
        return game.map(value -> gameService.mapGameToDTO(value, viewerPlayerId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Reveal community cards
    @PostMapping("/{id}/community")
    public ResponseEntity<GameStateDTO> revealCommunity(@PathVariable String id,
                                                        @RequestParam int count,
                                                        @RequestParam(required = false) String viewerPlayerId) {
        Optional<Game> game = gameService.revealCommunityCards(id, count);
        return game.map(value -> gameService.mapGameToDTO(value, viewerPlayerId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/{id}/state")
    public ResponseEntity<GameStateDTO> getGameState(@PathVariable String id,
                                                     @RequestParam(required = false) String viewerPlayerId) {
        Optional<Game> game = gameService.getGameState(id);
        return game.map(value -> gameService.mapGameToDTO(value, viewerPlayerId))
                .map(ResponseEntity::ok)
>>>>>>> Stashed changes
                .orElse(ResponseEntity.notFound().build());
    }
}
