package org.example.springboottest.Quiz.Entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "QuizResult")
@IdClass(QuizResultPk.class)
public class QuizResult {

    @Id
    private int quizId; // 主键

    @Id
    private String username; // 主键

    @Id
    @Column(name="record_time")
    private Date recordTime;

    private int E;
    private int I;
    private int S;
    private int N;
    private int T;
    private int F;
    private int J;
    private int P;
}