package org.example.springboottest.Whitenoise.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
/**
 * 音频实体类，对应数据库中的 Audio 表
 */
@Entity
@Data
@Table(name = "Audio")
public class Audio {
    /**
     * 音频名称
     */
    @Id
    @Column(name = "audio_name", length = 255)
    private String audioName;
    /**
     * 音频地址
     */
    @Column(name = "audio_src")
    private String audioSrc;
    /**
     * 音频图片
     */
    @Column(name = "audio_icon")
    private String audioIcon;
    /**
     * 情绪和对应特征值
     */
    private String emotion1;
    private String emotion2;
    private double feature1;
    private double feature2;
    /**
     * 白噪声记录的关联关系，一对多
     */
    @OneToMany(mappedBy = "audio", cascade = CascadeType.ALL)
    private List<WhiteNoise> whiteNoises;
    /**
     * 非持久化字段，用于存储情绪与特征的映射，方便逻辑处理
     */
    @Transient
    private Map<String, Double> features;

    public Audio(String audioName, String audioSrc, String audioIcon, String emotion1, String emotion2, double feature1, double feature2) {
        this.audioName = audioName;
        this.audioSrc = audioSrc;
        this.audioIcon = audioIcon;
        this.features = new HashMap<>();
        this.features.put(emotion1, feature1);
        this.features.put(emotion2, feature2);
        this.emotion1 = emotion1;
        this.emotion2 = emotion2;
        this.feature1 = feature1;
        this.feature2 = feature2;
    }

    public Audio() {
    }
}
