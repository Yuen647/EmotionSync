package org.example.springboottest.Game.Controller;

import org.example.springboottest.Game.Entity.GameState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gamestate")
public class GameStateController {

    @Autowired
    private GameStateRepository gameStateRepository;

    // 获取所有游戏记录
    @GetMapping("/")
    public List<GameState> getAllGameStates() {
        return gameStateRepository.findAll();
    }

    // 根据 ID 获取单个游戏记录
    @GetMapping("/{id}")
    public ResponseEntity<GameState> getGameStateById(@PathVariable("id") int id) {
        Optional<GameState> gameState = gameStateRepository.findById(id);
        return gameState.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 根据用户名和游戏类型查询
    @GetMapping("/user/{userName}/game/{gameType}")
    public ResponseEntity<GameState> getGameStateByUserAndGameType(@PathVariable String userName, @PathVariable String gameType) {
        GameState gameState = gameStateRepository.findByUserNameAndGameType(userName, gameType);
        if (gameState == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(gameState);
    }

    // 添加或更新游戏状态
    @PostMapping("/add")
    public ResponseEntity<GameState> addOrUpdateGameState(@RequestBody GameState gameState) {
        // 如果已存在相同用户和游戏类型的记录，则更新
        GameState existingGameState = gameStateRepository.findByUserNameAndGameType(gameState.getUserName(), gameState.getGameType());
        if (existingGameState != null) {
            existingGameState.setHighestScore(gameState.getHighestScore());
            existingGameState.setGameDuration(gameState.getGameDuration());
            existingGameState.setStartTime();
            gameStateRepository.save(existingGameState);
        } else {
            // 否则，保存新记录
            gameState.setStartTime();
            gameStateRepository.save(gameState);
        }
        return ResponseEntity.ok(gameState);
    }

    // 删除游戏状态
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteGameState(@PathVariable("id") int id) {
        if (!gameStateRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        gameStateRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}