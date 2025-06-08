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


    @Test
    @DisplayName("deleteGameState: 当删除已有记录时，应返回 200 OK 并包含被删 ID")
    void testDeleteGameState_Success() {
        // 对于 deleteById，controller 直接调用 repository.deleteById(id)，不抛异常时视为成功

        // —— 调用 controller 方法 ——
        ResponseEntity<Integer> response = gameController.deleteGameState(100);

        // —— 验证响应状态码为 200 OK ——
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 返回 body 应该是我们传入的 ID
        assertEquals(100, response.getBody());

        // 验证 repository.deleteById(...) 被调用一次
        Mockito.verify(gameStateRepository, Mockito.times(1)).deleteById(100);
    }


    @Test
    @DisplayName("getGameStatesByUsername: 当 repository 返回空列表时，应返回 404 NOT_FOUND")
    void testGetGameStatesByUsername_NotFound() {
        Mockito.when(gameStateRepository.findAllByUserName("charlie"))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<GameState>> response = gameController.getGameStatesByUsername("charlie");

        // 既然模拟为空，controller 应该返回 404
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody(), "body 应为 null");
    }

    @Test
    @DisplayName("addOrUpdateGameState：给无 startTime 的 GameState 自动设置时间，保存并返回 201")
    void testAddOrUpdateGameState() {
        // —— 1. 构造一个没有 startTime、id 的 GameState（Lombok 生成了 setter 方法） ——
        GameState input = new GameState();
        input.setUserName("testUser");        // Lombok 会生成这个 setter
        input.setGameType("Tetris");
        input.setHighestScore(150);
        input.setGameDuration(45);
        // 此时 input.getStartTime() 应为 null，input.getGameStateId() == 0

        // —— 2. 模拟 repository.save(...)：返回和传入的那个对象一样 ——
        Mockito.when(gameStateRepository.save(Mockito.any(GameState.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // —— 3. 调用 Controller 方法 ——
        ResponseEntity<GameState> response = gameController.addOrUpdateGameState(input);

        // —— 4. 验证：状态码应该是 201 Created ——
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // —— 5. 获取响应体并断言各字段 ——
        GameState result = response.getBody();
        assertNotNull(result, "返回的 GameState 不应为 null");

        // 5.1 基本字段要和我们输入的一致
        assertEquals("testUser", result.getUserName());
        assertEquals("Tetris", result.getGameType());
        assertEquals(150, result.getHighestScore());
        assertEquals(45, result.getGameDuration());

        // 5.2 setStartTime() 应该把 startTime 设为非空
        assertNotNull(result.getStartTime(), "startTime 应该被设置为当前时间");

        // —— 6. 验证 save(...) 被调用了一次 ——
        Mockito.verify(gameStateRepository, Mockito.times(1)).save(Mockito.any(GameState.class));
    }
    @Test
    @DisplayName("getAllGameStates: 当 repository.findAll() 返回两条记录时，应原样返回这两条")
    void testGetAllGameStates_Found() {
        // —— 1. 构造两条模拟数据 ——
        GameState state1 = new GameState();
        state1.setUserName("alice");            // Lombok 会生成这个 setter
        state1.setGameType("2048");
        state1.setHighestScore(200);
        state1.setGameDuration(120);
        // 如果想测试 startTime，也可以手动设置：
        state1.setStartTime();                  // 会把 startTime 设为 now

        GameState state2 = new GameState();
        state2.setUserName("bob");
        state2.setGameType("Tetris");
        state2.setHighestScore(300);
        state2.setGameDuration(90);
        state2.setStartTime();

        List<GameState> mockList = Arrays.asList(state1, state2);

        // —— 2. 模拟 repository 的行为 ——
        Mockito.when(gameStateRepository.findAll())
                .thenReturn(mockList);

        // —— 3. 调用 controller 方法 ——
        List<GameState> result = gameController.getAllGameStates();

        // —— 4. 验证返回值 ——
        assertNotNull(result, "返回的列表不应为 null");
        assertEquals(2, result.size(), "结果列表长度应为 2");

        // 验证第一条记录内容
        assertEquals("alice", result.get(0).getUserName());
        assertEquals("2048", result.get(0).getGameType());
        assertEquals(200, result.get(0).getHighestScore());
        assertEquals(120, result.get(0).getGameDuration());
        assertNotNull(result.get(0).getStartTime(), "第一条的 startTime 应该非空");

        // 验证第二条记录内容
        assertEquals("bob", result.get(1).getUserName());
        assertEquals("Tetris", result.get(1).getGameType());
        assertEquals(300, result.get(1).getHighestScore());
        assertEquals(90, result.get(1).getGameDuration());
        assertNotNull(result.get(1).getStartTime(), "第二条的 startTime 应该非空");

        // —— 5. 验证 findAll() 确实被调用了一次 ——
        Mockito.verify(gameStateRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("getAllGameStates: 当 repository.findAll() 返回空列表时，应返回空列表")
    void testGetAllGameStates_Empty() {
        // 模拟 repository 返回空列表
        Mockito.when(gameStateRepository.findAll())
                .thenReturn(Arrays.asList());

        List<GameState> result = gameController.getAllGameStates();

        // 返回的列表也应该是空
        assertNotNull(result, "空列表也不应为 null");
        assertTrue(result.isEmpty(), "结果列表应为空");

        Mockito.verify(gameStateRepository, Mockito.times(1)).findAll();
    }
    @Test
    @DisplayName("getGameStatesByUsername: 当 repository 返回非空列表时，应返回 200 OK + 列表内容")
    void testGetGameStatesByUsername_Found() {
        // —— 1. 构造两条模拟 GameState 记录 ——
        GameState state1 = new GameState();
        state1.setGameStateId(1);
        state1.setUserName("alice");
        state1.setGameType("2048");
        state1.setHighestScore(200);
        state1.setGameDuration(120);
        state1.setStartTime();  // 会把 startTime 设为 now

        GameState state2 = new GameState();
        state2.setGameStateId(2);
        state2.setUserName("alice");
        state2.setGameType("Tetris");
        state2.setHighestScore(150);
        state2.setGameDuration(90);
        state2.setStartTime();

        List<GameState> mockList = Arrays.asList(state1, state2);

        // —— 2. 模拟 repository.findAllByUserName(...) 返回 mockList ——
        Mockito.when(gameStateRepository.findAllByUserName("alice"))
                .thenReturn(mockList);

        // —— 3. 调用 controller 方法 ——
        ResponseEntity<List<GameState>> response = gameController.getGameStatesByUsername("alice");

        // —— 4. 验证响应状态码为 200 OK ——
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // —— 5. 验证返回的 body 不为空且与 mockList 一致 ——
        List<GameState> result = response.getBody();
        assertNotNull(result, "返回的列表不应为 null");
        assertEquals(2, result.size(), "列表长度应为 2");

        // 验证第一条记录
        GameState r1 = result.get(0);
        assertEquals(1, r1.getGameStateId());
        assertEquals("alice", r1.getUserName());
        assertEquals("2048", r1.getGameType());
        assertEquals(200, r1.getHighestScore());
        assertEquals(120, r1.getGameDuration());
        assertNotNull(r1.getStartTime());

        // 验证第二条记录
        GameState r2 = result.get(1);
        assertEquals(2, r2.getGameStateId());
        assertEquals("alice", r2.getUserName());
        assertEquals("Tetris", r2.getGameType());
        assertEquals(150, r2.getHighestScore());
        assertEquals(90, r2.getGameDuration());
        assertNotNull(r2.getStartTime());

        // —— 6. 验证 repository.findAllByUserName("alice") 被调用一次 ——
        Mockito.verify(gameStateRepository, Mockito.times(1)).findAllByUserName("alice");
    }
}
    
