package org.example.springboottest.Whitenoise.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
/**
 * 白噪声播放记录实体类，对应数据库中的 WhiteNoise 表
 */
@Entity
@Data
@Table(name = "WhiteNoise")
public class WhiteNoise {
    /**
     * 白噪声 ID，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 用户 ID
     */
    @Column(name = "user_id")
    private Long userId;
    /**
     * 音频实体关联，外键对应 Audio 表的 audio_name 字段
     */
    @ManyToOne
    @JoinColumn(name = "audio_name", referencedColumnName = "audio_name")
    private Audio audio;
    /**
     * 持续时长
     */
    @Column(name = "last_time")
    private int lastTime;
    /**
     * 情绪
     */
    @Column(name = "emotion")
    private String emotion;
    /**
     * 开始时间
     */
    @Column(name="starttime")
    private LocalDateTime startTime;
}