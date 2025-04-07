package org.example.springboottest.Quiz.Controller;

import org.example.springboottest.Quiz.Entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, String> {
}
