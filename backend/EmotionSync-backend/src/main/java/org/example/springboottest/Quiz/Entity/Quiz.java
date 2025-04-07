package org.example.springboottest.Quiz.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Quiz")
public class Quiz {
    @Id
    private int id; // 主键

    private String name;
}