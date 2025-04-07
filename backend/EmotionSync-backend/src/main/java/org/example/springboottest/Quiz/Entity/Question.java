package org.example.springboottest.Quiz.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Question")
public class Question {
    @Id
    private int questionId;

    private int quizId;

    private String description;

    private String dimension;
}
