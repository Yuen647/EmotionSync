package org.example.springboottest.Whitenoise.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Entity
@Data
@Table(name = "Audio")
public class Audio {
    @Id
    @Column(name = "audio_name", length = 255)
    private String audioName;

    @Column(name = "audio_src")
    private String audioSrc;

    @Column(name = "audio_icon")
    private String audioIcon;

    private String emotion1;
    private String emotion2;
    private double feature1;
    private double feature2;

    @OneToMany(mappedBy = "audio", cascade = CascadeType.ALL)
    private List<WhiteNoise> whiteNoises;

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
