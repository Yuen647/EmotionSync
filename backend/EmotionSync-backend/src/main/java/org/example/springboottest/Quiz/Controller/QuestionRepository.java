package org.example.springboottest.Quiz.Controller;

import org.example.springboottest.Quiz.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, String> {
    List<Question> findAllByQuizId(int quizId);
}
