package org.example.springboottest.Whitenoise.Controller;

import org.example.springboottest.Whitenoise.Service.EmotionRecommender;
import org.example.springboottest.Whitenoise.Service.WhiteNoiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api")
public class WhiteNoiseController {

    @Autowired
    private WhiteNoiseService whiteNoiseService;

    @PostMapping("/whitenoise")
    public ResponseEntity<Map<String, Object>> receiveWhiteNoiseData(@RequestBody Map<String, Object> requestData) {
        String username = (String) requestData.get("username");
        int playDuration = (int) requestData.get("playDuration");
        String emotion = (String) requestData.get("emotion");
        String audioName = (String) requestData.get("audioName");
        LocalDateTime now = LocalDateTime.now();

        System.out.println("用户名: " + username);
        System.out.println("播放时长: " + playDuration);
        System.out.println("情绪: " + emotion);
        System.out.println("音频名称: " + audioName);
        System.out.println("now: " + now);

        try{
            whiteNoiseService.saveWhiteNoise(username, playDuration, emotion, audioName, now);
            return ResponseEntity.status(200).body(Map.of("success", true, "message", "插入成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "数据库错误，请稍后再试"));
        }

    }
}

