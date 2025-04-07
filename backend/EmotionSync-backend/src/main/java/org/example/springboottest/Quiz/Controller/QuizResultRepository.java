package org.example.springboottest.Quiz.Controller;

import org.example.springboottest.Quiz.Entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizResultRepository extends JpaRepository<QuizResult, String> {
    List<QuizResult> findAllByQuizIdAndUsername(int quizId, String username);

    List<QuizResult> findAllByUsername(String username);
}
