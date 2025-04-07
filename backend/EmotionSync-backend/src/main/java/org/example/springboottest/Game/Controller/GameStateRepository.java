package org.example.springboottest.Game.Controller;

import org.example.springboottest.Game.Entity.GameState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameStateRepository extends JpaRepository<GameState, Integer> {

    // 可以根据需要添加自定义查询方法
    GameState findByUserNameAndGameType(String userName, String gameType);

    List<GameState> findAllByUserName(String userName);

    List<GameState> findAllByUserNameAndGameType(String username, String gameType);

    List<GameState> findAllByUserNameAndStartTimeBetween(String username, LocalDateTime startTime, LocalDateTime endTime);
}