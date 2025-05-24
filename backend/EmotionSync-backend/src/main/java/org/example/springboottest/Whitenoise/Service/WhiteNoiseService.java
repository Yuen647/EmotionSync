package org.example.springboottest.Whitenoise.Service;

import org.example.springboottest.User.Controller.UserRepository;
import org.example.springboottest.User.Entity.User;
import org.example.springboottest.Whitenoise.Controller.AudioRepository;
import org.example.springboottest.Whitenoise.Controller.WhiteNoiseRepository;
import org.example.springboottest.Whitenoise.Entity.Audio;
import org.example.springboottest.Whitenoise.Entity.WhiteNoise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WhiteNoiseService {

    private final WhiteNoiseRepository whiteNoiseRepository;
    private final UserRepository userRepository;
    private final AudioRepository audioRepository;

    // 使用构造器注入，推荐Spring的最佳实践，方便单元测试
    public WhiteNoiseService(WhiteNoiseRepository whiteNoiseRepository,
                             UserRepository userRepository,
                             AudioRepository audioRepository) {
        this.whiteNoiseRepository = whiteNoiseRepository;
        this.userRepository = userRepository;
        this.audioRepository = audioRepository;
    }
    /**
     * 保存用户白噪音播放记录
     *
     * @param username 用户名
     * @param playDuration 播放时长（秒）
     * @param emotion 用户情绪
     * @param audioName 音频名称
     * @param now 播放开始时间
     */
    public void saveWhiteNoise(String username, int playDuration, String emotion, String audioName, LocalDateTime now) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }

        Audio audio = audioRepository.findByAudioName(audioName);
        if (audio == null) {
            throw new RuntimeException("Audio not found with name: " + audioName);
        }
        
        WhiteNoise whiteNoise = new WhiteNoise();
        whiteNoise.setUserId(user.getUserId());
        whiteNoise.setLastTime(playDuration);
        whiteNoise.setEmotion(emotion);
        whiteNoise.setAudio(audio);
        whiteNoise.setStartTime(now);

        whiteNoiseRepository.save(whiteNoise);
    }
}

