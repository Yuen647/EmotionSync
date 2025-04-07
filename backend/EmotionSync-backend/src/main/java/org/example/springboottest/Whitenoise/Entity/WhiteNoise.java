package org.example.springboottest.Whitenoise.Entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "WhiteNoise")
@IdClass(WhiteNoisePk.class)
public class WhiteNoise {
    @Id
    @Column(name = "username")
    private String username; // 主键

    @Id
    @Column(name = "audio_name")
    private String audio_name;

    @Id
    @Column(name = "last_time")
    private int last_time;

    @Id
    @Column(name = "emotion")
    private String emotion;

    @Column(name="starttime")
    private LocalDateTime starttime;

}