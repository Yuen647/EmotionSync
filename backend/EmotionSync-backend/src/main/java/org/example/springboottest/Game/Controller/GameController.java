// GameController.java
package org.example.springboottest.Game.Controller;

import org.example.springboottest.Game.Entity.GameState;
import org.example.springboottest.Game.Controller.GameStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api/gamestate")
public class GameController {

    @Autowired
    private GameStateRepository gameStateRepository;

    @GetMapping
    public List<GameState> getAllGameStates() {
        return gameStateRepository.findAll();
    }

    @GetMapping("/{username}/{gameType}")
    public ResponseEntity<GameState> getGameStateByUserAndGameType(
            @PathVariable String username,
            @PathVariable String gameType) {

        List<GameState> gameStates = gameStateRepository.findAllByUserNameAndGameType(username, gameType);
        if (gameStates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        GameState result = new GameState();
        result.setUserName(username);
        result.setGameType(gameType);
        result.setGameDuration(
                gameStates.stream().mapToInt(GameState::getGameDuration).sum()
        );
        result.setHighestScore(
                gameStates.stream().mapToInt(GameState::getHighestScore).max().orElse(0)
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    public ResponseEntity<GameState> addOrUpdateGameState(@RequestBody GameState gameState) {
        gameState.setStartTime();
        gameStateRepository.save(gameState);
        return ResponseEntity.status(HttpStatus.CREATED).body(gameState);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteGameState(@PathVariable int id) {
        gameStateRepository.deleteById(id);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<GameState>> getGameStatesByUsername(@PathVariable String username) {
        List<GameState> gameStates = gameStateRepository.findAllByUserName(username);
        if (gameStates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(gameStates);
    }
}
