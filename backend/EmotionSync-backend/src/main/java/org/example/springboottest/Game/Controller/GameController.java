package org.example.springboottest.Game.Controller;

import org.example.springboottest.Game.Entity.GameState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gamestate")
@CrossOrigin(origins = "http://localhost:5173")  // 允许 Vue 3 前端跨域请求
public class GameController {

    @Autowired
    private GameStateRepository gameStateRepository;

    @GetMapping("/")
    public List<GameState> getAllGameStates() {
        return gameStateRepository.findAll();
    }

    @GetMapping("/{username}/{gameType}")
    public ResponseEntity<GameState> getGameStateByUserAndGameType(@PathVariable String username, @PathVariable String gameType) {
        List<GameState> gameStates = gameStateRepository.findAllByUserNameAndGameType(username, gameType);
        GameState gameState1 = new GameState();
        gameState1.setUserName(username);
        gameState1.setGameType(gameType);

        Integer sum=0;
        for (GameState gs : gameStates) {
            // 处理每个GameState对象
            sum+=gs.getGameDuration();
        }
        gameState1.setGameDuration(sum);

        // 找到最高分
        Integer highestScore = gameStates.stream()
                .mapToInt(GameState::getHighestScore)
                .max()
                .orElse(0);  // 如果没有记录则返回默认值0
        gameState1.setHighestScore(highestScore);

        if (gameState1 != null) {
            return ResponseEntity.ok(gameState1);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping("/add")
    public ResponseEntity<GameState> addOrUpdateGameState(@RequestBody GameState gameState) {
        gameState.setStartTime();
        gameStateRepository.save(gameState);
        return ResponseEntity.ok(gameState);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameState(@PathVariable int id) {
        gameStateRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}