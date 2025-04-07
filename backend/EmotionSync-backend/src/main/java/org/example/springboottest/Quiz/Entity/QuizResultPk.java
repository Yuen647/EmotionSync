package org.example.springboottest.Quiz.Entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class QuizResultPk implements Serializable {
    private int quizId; // 主键
    private String username; // 主键
    private Date recordTime;
}