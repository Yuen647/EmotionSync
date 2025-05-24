package org.example.springboottest.Whitenoise.Service;

import org.example.springboottest.Whitenoise.Controller.AudioRepository;
import org.example.springboottest.Whitenoise.Entity.Audio;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class EmotionRecommender {

    // 音频数据存储
    private Map<String, Audio> items;
    private final AudioRepository audioRepository;
    // 预设的音频名称列表
    private final List<String> audios =  new ArrayList<>(Arrays.asList("rain", "wind", "thunder","waves","forest","fire","cafe","crickets","boating"));

    public EmotionRecommender(AudioRepository audioRepository) {
        this.audioRepository = audioRepository;
    }
    /**
     * 初始化音频数据，从数据库读取并缓存
     */
    private void init() {
        items = new HashMap<>();
        for (String audioName : audios) {
            Audio audio = audioRepository.findByAudioName(audioName);
            items.put(audioName, new Audio(audio.getAudioName(), audio.getAudioSrc(), audio.getAudioIcon(), audio.getEmotion1(), audio.getEmotion2(), audio.getFeature1(), audio.getFeature2()));
        }
    }
    /**
     * 计算两个向量的余弦相似度
     * @param userVector 用户情绪向量
     * @param itemVector 音频特征向量
     * @return 相似度，范围[0,1]
     */
    private double cosineSimilarity(Map<String, Double> userVector, Map<String, Double> itemVector) {
        double dotProduct = 0.0;
        double userNorm = 0.0;
        double itemNorm = 0.0;

        for (String key : userVector.keySet()) {
            double userValue = userVector.getOrDefault(key, 0.0);
            double itemValue = itemVector.getOrDefault(key, 0.0);

            dotProduct += userValue * itemValue;
            userNorm += userValue * userValue;
        }

        for (double itemValue : itemVector.values()) {
            itemNorm += itemValue * itemValue;
        }

        if (userNorm == 0.0 || itemNorm == 0.0) {
            return 0.0; // 避免分母为 0
        }

        return dotProduct / (Math.sqrt(userNorm) * Math.sqrt(itemNorm));
    }

    /**
     * 根据用户情绪向量推荐音频列表
     * @param userEmotion 用户情绪向量
     * @param numRecommendations 推荐数量
     * @return 推荐音频列表
     */
    public List<Audio> recommend(Map<String, Double> userEmotion, int numRecommendations) {
        // 进行初始化
        init();
        List<Map.Entry<Audio, Double>> scoredItems = new ArrayList<>();

        // 计算余弦相似度
        for (Audio item : items.values()) {
            double score = cosineSimilarity(userEmotion, item.getFeatures());
            scoredItems.add(Map.entry(item, score));
        }

        // 按相似度得分排序
        scoredItems.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        // 提取前 numRecommendations 个音频
        List<Audio> recommendations = new ArrayList<>();
        for (int i = 0; i < Math.min(numRecommendations, scoredItems.size()); i++) {
            recommendations.add(scoredItems.get(i).getKey());
        }

        return recommendations;
    }

}
