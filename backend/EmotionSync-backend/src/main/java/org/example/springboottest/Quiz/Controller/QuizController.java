package org.example.springboottest.Quiz.Controller;


import org.example.springboottest.Quiz.Entity.Question;
import org.example.springboottest.Quiz.Entity.Quiz;
import org.example.springboottest.Quiz.Entity.QuizResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/quiz")
public class QuizController {
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @PostMapping("/getQuizNames")
    public List<Quiz> getQuizNames() {
        return quizRepository.findAll();
    }

    @PostMapping("/getQuizResult")
    public Boolean getQuizResult(@RequestBody Map<String, String> requestBody){
        int quizId = Integer.parseInt(requestBody.get("quizId"));
        String username = requestBody.get("username");
        return !(quizResultRepository.findAllByQuizIdAndUsername(quizId, username).isEmpty());
    }

    @PostMapping("/getQuestions")
    public List<Question> getQuestions(@RequestBody Map<String, String> requestBody) {
        return questionRepository.findAllByQuizId(Integer.parseInt(requestBody.get("id")));
    }

    @PostMapping("/submitQuiz")
    public ResponseEntity<String> submitQuiz(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String quizIdStr = requestBody.get("quizId");
        if (username == null || quizIdStr == null) {
            return ResponseEntity.badRequest().body("Username or quizId null.");
        }
        int quizId = Integer.parseInt(quizIdStr);
        QuizResult quizResult = new QuizResult();
        quizResult.setQuizId(quizId);
        quizResult.setUsername(username);
        quizResult.setRecordTime(new Date());
        quizResult.setE(quizResult.getE() + Integer.parseInt(requestBody.get("E")));
        quizResult.setI(quizResult.getI() + Integer.parseInt(requestBody.get("I")));
        quizResult.setS(quizResult.getS() + Integer.parseInt(requestBody.get("S")));
        quizResult.setN(quizResult.getN() + Integer.parseInt(requestBody.get("N")));
        quizResult.setT(quizResult.getT() + Integer.parseInt(requestBody.get("T")));
        quizResult.setF(quizResult.getF() + Integer.parseInt(requestBody.get("F")));
        quizResult.setJ(quizResult.getJ() + Integer.parseInt(requestBody.get("J")));
        quizResult.setP(quizResult.getP() + Integer.parseInt(requestBody.get("P")));
        quizResultRepository.save(quizResult);

        return ResponseEntity.ok("Data added successfully!");
    }
}
