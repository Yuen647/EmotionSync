package org.example.springboottest.Game.Controller;

import org.example.springboottest.Game.Entity.GameState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameStateRepository extends JpaRepository<GameState, Integer> {

    /**
     * 获取用户的某游戏状态
     * @param userName 用户名
     * @param gameType 游戏类型
     * @return 若存在返回该用户的某游戏状态
     */
    GameState findByUserNameAndGameType(String userName, String gameType);

    /**
     * 获取用户的某游戏状态
     * @param userName 用户名
     * @return 若存在返回该用户的游戏状态
     */
    List<GameState> findAllByUserName(String userName);

    /**
     * 获取某用户的游戏状态
     * @param username 用户名
     * @param gameType 游戏类型
     * @return 返回该用户的游戏状态
     */
    List<GameState> findAllByUserNameAndGameType(String username, String gameType);

    /**
     * 返回用户在某时间进行游玩的状态
     * @param username 用户名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 若存在返回该用户在该时间段的游戏状态
     */
    List<GameState> findAllByUserNameAndStartTimeBetween(String username, LocalDateTime startTime, LocalDateTime endTime);
}