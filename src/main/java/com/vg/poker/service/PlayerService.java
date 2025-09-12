package com.vg.poker.service;

import com.vg.poker.entity.Player;
import com.vg.poker.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final MongoTemplate mongoTemplate;

    public ResponseEntity<Player> addPlayer(Player player) {
        if(!StringUtils.hasLength(player.getName())){
            throw new IllegalArgumentException("Player name is required!");
        }

        Player savedPlayer = playerRepository.save(player);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlayer);
    }

    public ResponseEntity<Player> findPlayerById(String id) {
        Optional<Player> player = playerRepository.findById(id);

        if(player.isEmpty()){
            throw new NoSuchElementException();
        }

        return ResponseEntity.ok(player.get());
    }

    public List<Player> findPlayersWithChips(Integer minChips, Integer maxChips, Integer chips) {
        Query query = new Query();

        if(chips!=null){
            query.addCriteria(Criteria.where("chips").is(chips));
        }
        else {
            if(minChips!=null){
                query.addCriteria(Criteria.where("chips").gte(minChips));
            }
            if(maxChips!=null){
                query.addCriteria(Criteria.where("chips").lte(maxChips));
            }
        }

        return mongoTemplate.find(query, Player.class);
    }
}
