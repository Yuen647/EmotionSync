package org.example.springboottest.Game.Entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "GameState")
@Data
public class GameState implements Serializable {

    /**
     * 用户 ID，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int gameStateId;

    /**
     * 用户名
     */
    @Column(name = "username")
    private String userName;

    /**
     * 游戏类型
     */
    @Column(name = "game_type")
    private String gameType;

    /**
     * 最高分
     */
    @Column(name = "highest_score")
    private Integer highestScore;

    /**
     * 游戏时长
     */
    @Column(name = "game_duration")
    private Integer gameDuration;

    /**
     * 起始时间
     */
    @Column(name="start_time")
    private LocalDateTime startTime;

    public void setStartTime() {
        this.startTime = LocalDateTime.now();
        System.out.println(startTime);
    }
}