package org.example.springboottest.User.Controller;

import org.example.springboottest.Game.Controller.GameStateRepository;
import org.example.springboottest.Game.Entity.GameState;
import org.example.springboottest.Quiz.Controller.QuizResultRepository;
import org.example.springboottest.Quiz.Entity.QuizResult;
import org.example.springboottest.User.Entity.User;
import org.example.springboottest.Whitenoise.Controller.WhiteNoiseRepository;
import org.example.springboottest.Whitenoise.Entity.WhiteNoise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private QuizResultRepository quizResultRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private WhiteNoiseRepository whiteNoiseRepository;
    @Autowired
    private GameStateRepository gameStateRepository;

    @PostMapping("/email")
    public String getEmail(@RequestBody Map<String, String> requestData) {
        String username = requestData.get("username");
        return profileRepository.findById(username)
                .map(User::getEmail) // 提取 email 字段
                .orElse("User not found");  // 如果没有找到记录，返回提示信息
    }

    private String calculateIdentity(List<QuizResult> results) {
        if (results.isEmpty()) {
            return "未进行心理小测试";
        }
        int EI = 0;
        int SN = 0;
        int TF = 0;
        int JP = 0;
        for (QuizResult result : results) {
            EI += result.getE() - result.getI();
            SN += result.getS() - result.getN();
            TF += result.getT() - result.getF();
            JP += result.getJ() - result.getP();
        }
        return (EI > 0 ? "E" : "I") +
                (SN > 0 ? "S" : "N") +
                (TF > 0 ? "T" : "F") +
                (JP > 0 ? "J" : "P");
    }

    @PostMapping("/mbti")
    public String getIdentity(@RequestBody Map<String, String> requestData) {
        List<QuizResult> results = quizResultRepository.findAllByUsername(requestData.get("username"));
        return calculateIdentity(results);
    }
    /*
    @PostMapping("/whitenoise")
    public Map<String, Integer> getWhiteNoise(@RequestBody Map<String, String> requestData) {
        String username = requestData.get("username");
        List<WhiteNoise> whitenoises = whiteNoiseRepository.findAllByUsername(username);
        Map<String, Integer> wnTime = new HashMap<>();
        for (WhiteNoise record : whitenoises)
            wnTime.merge(record.getAudio_name(), record.getLast_time(), Integer::sum);
        return wnTime;
    }

    @PostMapping("/emotion")
    public Map<String, Integer> getEmotion(@RequestBody Map<String, String> requestData) {
        String username = requestData.get("username");
        List<WhiteNoise> whitenoises = whiteNoiseRepository.findAllByUsername(username);
        Map<String, Integer> wnTime = new HashMap<>();
        for (WhiteNoise record : whitenoises)
            wnTime.merge(record.getEmotion(), 1, Integer::sum);
        return wnTime;
    }

     */

    @PostMapping("/gametime")
    public Map<String, Integer> getGameDuration(@RequestBody Map<String, String> requestData) {
        String username = requestData.get("username");
        List<GameState> gameStates = gameStateRepository.findAllByUserName(username);
        Map<String, Integer> result = new HashMap<>();
        for (GameState gameState : gameStates)
            result.merge(gameState.getGameType(), gameState.getGameDuration(), Integer::sum);
        return result;
    }

    @PostMapping("/allWhiteNoise")
    public Map<String, Integer> getAllWhiteNoise() {
        List<WhiteNoise> whitenoises = whiteNoiseRepository.findAll();
        Map<String, Integer> wnTime = new HashMap<>();
        for (WhiteNoise record : whitenoises)
            wnTime.merge(record.getAudioName(), record.getLastTime(), Integer::sum);
        return wnTime;
    }

    @PostMapping("/allEmotion")
    public Map<String, Integer> getAllEmotion() {
        List<WhiteNoise> whitenoises = whiteNoiseRepository.findAll();
        Map<String, Integer> wnTime = new HashMap<>();
        for (WhiteNoise record : whitenoises)
            wnTime.merge(record.getEmotion(), 1, Integer::sum);
        return wnTime;
    }

    @PostMapping("/allGameTime")
    public Map<String, Integer> getAllGameDuration() {
        List<GameState> gameStates = gameStateRepository.findAll();
        Map<String, Integer> result = new HashMap<>();
        for (GameState gameState : gameStates)
            result.merge(gameState.getGameType(), gameState.getGameDuration(), Integer::sum);
        return result;
    }
    /*
    @PostMapping("/timedwhitenoise")
    public Map<String, Integer> getPartTimeWhiteNoise(@RequestBody Map<String, String> requestData) {
        String username = requestData.get("username");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(requestData.get("startTime"), formatter);
        LocalDateTime endTime = LocalDateTime.parse(requestData.get("endTime"), formatter);
        List<WhiteNoise> whitenoises = whiteNoiseRepository.findAllByUsernameAndStarttimeBetween(username, startTime, endTime);
        Map<String, Integer> wnTime = new HashMap<>();
        for (WhiteNoise record : whitenoises)
            wnTime.merge(record.getAudio_name(), record.getLast_time(), Integer::sum);
        return wnTime;
    }

    @PostMapping("/timedEmotion")
    public Map<String, Integer> getPartTimeEmotion(@RequestBody Map<String, String> requestData) {
        String username = requestData.get("username");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(requestData.get("startTime"), formatter);
        LocalDateTime endTime = LocalDateTime.parse(requestData.get("endTime"), formatter);
        List<WhiteNoise> whitenoises = whiteNoiseRepository.findAllByUsernameAndStarttimeBetween(username, startTime, endTime);
        Map<String, Integer> wnTime = new HashMap<>();
        for (WhiteNoise record : whitenoises)
            wnTime.merge(record.getEmotion(), 1, Integer::sum);
        return wnTime;
    }
    */
    @PostMapping("/timedgametime")
    public Map<String, Integer> getPartTimeGameDuration(@RequestBody Map<String, String> requestData) {
        String username = requestData.get("username");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(requestData.get("startTime"), formatter);
        LocalDateTime endTime = LocalDateTime.parse(requestData.get("endTime"), formatter);
        List<GameState> gameStates = gameStateRepository.findAllByUserNameAndStartTimeBetween(username, startTime, endTime);
        Map<String, Integer> result = new HashMap<>();
        for (GameState gameState : gameStates)
            result.merge(gameState.getGameType(), gameState.getGameDuration(), Integer::sum);
        return result;
    }
}
