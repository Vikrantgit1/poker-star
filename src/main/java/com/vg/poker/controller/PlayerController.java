package com.vg.poker.controller;

import com.vg.poker.entity.Player;
import com.vg.poker.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping("/")
    public ResponseEntity<Player> addPlayer(@RequestBody Player player){
        return playerService.addPlayer(player);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> findPlayerById(@PathVariable String id){
        return playerService.findPlayerById(id);
    }

    @GetMapping("/findAllByChips")
    public ResponseEntity<List<Player>> findAllPlayersWithChips(
            @RequestParam(required = false) Integer minChips,
            @RequestParam(required = false) Integer maxChips,
            @RequestParam(required = false) Integer chips
    ){
        return ResponseEntity.ok(playerService.findPlayersWithChips(
                minChips, maxChips, chips
        ));
    }
}
