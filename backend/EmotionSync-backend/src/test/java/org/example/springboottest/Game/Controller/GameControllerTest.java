package org.example.springboottest.Game.Controller;

import org.example.springboottest.Game.Entity.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class GameControllerTest {
    @Mock
    private GameStateRepository gameStateRepository;

    // 2. 直接 new 一个 GameController 实例，在@BeforeEach 里把 mock 注入进去
    private GameController gameController;

    @BeforeEach
    void setUp() {
        // 初始化 @Mock 注解
        MockitoAnnotations.openMocks(this);

        // 把 mock 的 repository 传给 controller
        gameController = new GameController();
        gameController.gameStateRepository = this.gameStateRepository;
    }

    @Test
    @DisplayName("当 repository 存在多条记录时，应合并后返回 200 + GameState")
    void testGetGameStateByUserAndGameType_Found() {
        // —— 准备两条模拟“数据库记录” ——
        GameState rec1 = new GameState();
        rec1.setGameStateId(1);
        rec1.setUserName("alice");
        rec1.setGameType("2048");
        rec1.setGameDuration(30);
        rec1.setHighestScore(80);

        GameState rec2 = new GameState();
        rec2.setGameStateId(2);
        rec2.setUserName("alice");
        rec2.setGameType("2048");
        rec2.setGameDuration(50);
        rec2.setHighestScore(120);

        List<GameState> mockList = Arrays.asList(rec1, rec2);

        // 当调用 findAllByUserNameAndGameType("alice","2048") 时，返回上面那两条记录
        Mockito.when(gameStateRepository.findAllByUserNameAndGameType("alice", "2048"))
                .thenReturn(mockList);

        // —— 直接调用 controller 方法 ——
        ResponseEntity<GameState> response = gameController.getGameStateByUserAndGameType("alice", "2048");

        // —— 验证返回状态码是 200 OK ——
        assertEquals(HttpStatus.OK, response.getStatusCode());

        GameState body = response.getBody();
        assertNotNull(body);

        // 应该把 gameDuration = 30 + 50 = 80
        assertEquals(80, body.getGameDuration());
        // 应该把 highestScore = max(80, 120) = 120
        assertEquals(120, body.getHighestScore());

        // 此 controller 里虽然新建了一个 result 对象，但只 set 了 userName/gameType/duration/score
        // 所以：
        assertEquals("alice", body.getUserName());
        assertEquals("2048", body.getGameType());

        // 既然没有 setStartTime()，startTime 应该还是 null
        assertNull(body.getStartTime());
        // 既然没有给 result.setGameStateId(...)，id 也应保持默认值 0
        assertEquals(0, body.getGameStateId());
    }

    @Test
    @DisplayName("当 repository 返回空列表时，应返回 404 NOT_FOUND")
    void testGetGameStateByUserAndGameType_NotFound() {
        // 模拟没有任何记录
        Mockito.when(gameStateRepository.findAllByUserNameAndGameType("bob", "ctr"))
                .thenReturn(Collections.emptyList());

        ResponseEntity<GameState> response = gameController.getGameStateByUserAndGameType("bob", "ctr");

        // 这个时候，controller 应该返回 404
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // body 应该是 null
        assertNull(response.getBody());
    }
}
    
