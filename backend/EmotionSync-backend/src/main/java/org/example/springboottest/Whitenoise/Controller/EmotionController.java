package org.example.springboottest.Whitenoise.Controller;

import org.example.springboottest.Whitenoise.Entity.Audio;
import org.example.springboottest.Whitenoise.Service.EmotionRecommender;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api/whitenoise")
public class EmotionController {


    private final EmotionRecommender recommenderService;

    public EmotionController(EmotionRecommender recommenderService) {
        this.recommenderService = recommenderService;
    }

    /**
     * 接收情绪请求，返回对应的音频推荐列表
     *
     * @param request 用户情绪请求，包含情绪标签及推荐数量
     * @return 推荐的音频列表
     */
    @PostMapping("/recommend")
    public List<Audio> recommendAudio(@RequestBody EmotionRequest request) {
        Map<String, Double> userEmotionVector = new HashMap<>();
        userEmotionVector.put(request.getEmotion(), 1.0); // 将用户情绪作为向量表示
        return recommenderService.recommend(userEmotionVector, request.getNumRecommendations());
    }

    /**
     * 内部静态类，用于接收情绪推荐请求体
     */
    public static class EmotionRequest {
        private String emotion;
        private int numRecommendations;

        public EmotionRequest(String emotion, int numRecommendations) {
            this.emotion = emotion;
            this.numRecommendations = numRecommendations;
        }

        public String getEmotion() {
            return emotion;
        }

        public void setEmotion(String emotion) {
            this.emotion = emotion;
        }

        public int getNumRecommendations() {
            return numRecommendations;
        }
    }
}
