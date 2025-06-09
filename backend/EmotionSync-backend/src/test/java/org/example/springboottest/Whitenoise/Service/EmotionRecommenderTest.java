package org.example.springboottest.Whitenoise.Service;

import org.example.springboottest.Whitenoise.Controller.AudioRepository;
import org.example.springboottest.Whitenoise.Entity.Audio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmotionRecommenderTest {

    private AudioRepository audioRepository;
    private EmotionRecommender recommender;

    @BeforeEach
    void setUp() {
        audioRepository = mock(AudioRepository.class);
        recommender = new EmotionRecommender(audioRepository);

        // 设置9个模拟音频返回
        for (String name : List.of("rain", "wind", "thunder", "waves", "forest", "fire", "cafe", "crickets", "boating")) {
            Audio mockAudio = new Audio(name, name + ".mp3", name + ".svg", "happy", "excited", 0.8, 0.2);
            when(audioRepository.findByAudioName(name)).thenReturn(mockAudio);
        }
    }

    @Test
    void recommend_withValidEmotion_returnsTopN() {
        Map<String, Double> userEmotion = Map.of("calm", 1.0);
        List<Audio> result = recommender.recommend(userEmotion, 3);
        assertEquals(3, result.size());
        assertNotNull(result.get(0));
    }

    @Test
    void recommend_withUnknownEmotion_returnsZero() {
        Map<String, Double> userEmotion = Map.of("angry", 1.0); // 音频中无匹配情绪
        List<Audio> result = recommender.recommend(userEmotion, 3);
        // 所有得分为0也仍会返回最多3条，只是排序最低
        assertEquals(3, result.size());
    }

    @Test
    void recommend_withZeroRequest_returnsEmpty() {
        Map<String, Double> userEmotion = Map.of("calm", 1.0);
        List<Audio> result = recommender.recommend(userEmotion, 0);
        assertTrue(result.isEmpty());
    }

    @Test
    void recommend_withEmptyEmotionVector_returnsZeroScores() {
        Map<String, Double> userEmotion = Map.of();
        List<Audio> result = recommender.recommend(userEmotion, 3);
        assertEquals(3, result.size()); // 仍返回3个，但得分都为0
    }

    @Test
    void recommend_requestMoreThanAvailable_returnsAll() {
        Map<String, Double> userEmotion = Map.of("calm", 1.0);
        List<Audio> result = recommender.recommend(userEmotion, 20); // 超过9个音频
        assertEquals(9, result.size());
    }
}
