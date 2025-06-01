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

    /**
     * 获取用户的某具体游戏数据
     * 接收username和gameType
     * 返回游戏数据
     */
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

    /**
     * 添加用户的游戏数据
     * 接收gamestate，即游戏数据核用户数据
     * 返回是否成功
     */
    @PostMapping("/add")
    public ResponseEntity<GameState> addOrUpdateGameState(@RequestBody GameState gameState) {
        gameState.setStartTime();
        gameStateRepository.save(gameState);
        return ResponseEntity.status(HttpStatus.CREATED).body(gameState);
    }

    /**
     * 删除用户的游戏数据
     * 接收userid
     * 返回是否成功
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteGameState(@PathVariable int id) {
        gameStateRepository.deleteById(id);
        return ResponseEntity.ok(id);
    }

    /**
     * 获取用户的历史游戏数据
     * 接收username
     * 返回游戏数据
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<List<GameState>> getGameStatesByUsername(@PathVariable String username) {
        List<GameState> gameStates = gameStateRepository.findAllByUserName(username);
        if (gameStates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(gameStates);
    }
}
