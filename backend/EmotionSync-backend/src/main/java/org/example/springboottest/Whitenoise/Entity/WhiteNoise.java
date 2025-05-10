package org.example.springboottest.Whitenoise.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "WhiteNoise")
public class WhiteNoise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "audio_name", referencedColumnName = "audio_name")
    private Audio audio;

    @Column(name = "last_time")
    private int lastTime;

    @Column(name = "emotion")
    private String emotion;

    @Column(name="starttime")
    private LocalDateTime startTime;
}