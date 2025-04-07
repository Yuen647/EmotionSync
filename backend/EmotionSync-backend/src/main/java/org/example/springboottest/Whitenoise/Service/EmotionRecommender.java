package org.example.springboottest.Whitenoise.Service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class EmotionRecommender {

    // 音频数据存储
    private final Map<String, Audio> items;

    // 初始化音频数据
    public EmotionRecommender() {
        items = new HashMap<>();
        // 音频数据与心情特征映射
        items.put("rain", new Audio("rain", "https://ambicular.com/sounds/rain/splashing-rainfall160.mp3", "https://defonic.b-cdn.net/defonic/images/icons/rain.svg", Map.of(
                "sad", 0.9, "relaxed", 0.6
        )));
        items.put("wind", new Audio("wind", "https://st2.asoftmurmur.com/assets/p/content/wind/main-wind.mp4","https://defonic.b-cdn.net/defonic/images/icons/wind.svg", Map.of(
                "relaxed", 0.8, "tired", 0.7
        )));
        items.put("thunder", new Audio("thunder", "https://st2.asoftmurmur.com/assets/p/content/thunder/main-thunder.mp4","https://defonic.b-cdn.net/defonic/images/icons/thunder.svg", Map.of(
                "angry", 0.9, "fear", 0.7
        )));
        items.put("waves", new Audio("waves","https://st3.asoftmurmur.com/assets/p/content/waves/glue-waves.mp4", "https://defonic.b-cdn.net/defonic/images/icons/brook.svg", Map.of(
                "happy", 0.8, "relaxed", 0.5
        )));
        items.put("forest", new Audio("forest","https://st2.asoftmurmur.com/assets/p/content/birds/main-birds.mp4", "https://defonic.b-cdn.net/defonic/images/icons/forest.svg", Map.of(
                "relaxed", 0.9, "happy", 0.7
        )));
        items.put("fire", new Audio("fire","https://st2.asoftmurmur.com/assets/p/content/fire/main-fire.mp4","https://defonic.b-cdn.net/defonic/images/icons/fire.svg",  Map.of(
                "relaxed", 0.9, "tired", 0.8
        )));
        items.put("cafe", new Audio("cafe","https://st3.asoftmurmur.com/assets/p/content/people/main-people.mp4","https://defonic.b-cdn.net/defonic/images/icons/cafe.svg",  Map.of(
                "happy", 0.7, "excited", 0.6
        )));
        items.put("crickets", new Audio("crickets","https://st3.asoftmurmur.com/assets/p/content/crickets/glue-crickets.mp4","https://defonic.b-cdn.net/defonic/images/icons/leafs.svg",  Map.of(
                "worry", 0.8, "tired", 0.7
        )));
        items.put("boating", new Audio("boating","https://ambicular.com/sounds/defonicprem/rowing160.mp3","https://defonic.b-cdn.net/defonic/images/icons/ocean.svg",  Map.of(
                "anxious", 0.8, "happy", 0.6
        )));
    }

    // 计算余弦相似度
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

    // 根据情绪向量推荐音频
    public List<Audio> recommend(Map<String, Double> userEmotion, int numRecommendations) {
        List<Map.Entry<Audio, Double>> scoredItems = new ArrayList<>();

        // 计算余弦相似度
        for (Audio item : items.values()) {
            double score = cosineSimilarity(userEmotion, item.features);
            scoredItems.add(Map.entry(item, score));
        }

        // 按相似度得分排序
        scoredItems.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        // 提取前 numRecommendations 个音频
        List<Audio> recommendations = new ArrayList<>();
        for (int i = 0; i < Math.min(numRecommendations, scoredItems.size()); i++) {
            recommendations.add(scoredItems.get(i).getKey());
            System.out.println(scoredItems.get(i).getKey().getId());
        }

        return recommendations;
    }

    // 内部类：音频物品
    public static class Audio {
        private final String id;
        private final String src;
        private final String icon;
        private final Map<String, Double> features;

        public Audio(String id, String src, String icon, Map<String, Double> features) {
            this.id = id;
            this.src = src;
            this.icon = icon;
            this.features = features;
        }

        public String getId() {
            return id;
        }

        public Map<String, Double> getFeatures() {
            return features;
        }

        public String getSrc() {
            return src;
        }

        public String getIcon() {
            return icon;
        }
    }
}
