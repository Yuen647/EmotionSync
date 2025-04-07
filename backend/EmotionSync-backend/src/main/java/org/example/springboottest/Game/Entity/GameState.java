package org.example.springboottest.Game.Entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "GameState")
@Data
public class GameState implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int gameStateId;

    @Column(name = "username")
    private String userName;

    @Column(name = "game_type")
    private String gameType;

    @Column(name = "highest_score")
    private Integer highestScore;

    @Column(name = "game_duration")
    private Integer gameDuration;

    @Column(name="start_time")
    private LocalDateTime startTime;

    public void setStartTime() {
        this.startTime = LocalDateTime.now();
        System.out.println(startTime);
    }
}