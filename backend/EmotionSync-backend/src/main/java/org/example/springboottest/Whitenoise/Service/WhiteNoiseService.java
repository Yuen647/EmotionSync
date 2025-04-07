package org.example.springboottest.Whitenoise.Service;

import org.example.springboottest.Whitenoise.Controller.WhiteNoiseRepository;
import org.example.springboottest.Whitenoise.Entity.WhiteNoise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WhiteNoiseService {

    @Autowired
    private WhiteNoiseRepository whiteNoiseRepository;

    public void saveWhiteNoise(String username, int playDuration, String emotion, String audioName, LocalDateTime now) {
        WhiteNoise whiteNoise = new WhiteNoise();
        whiteNoise.setUsername(username);
        whiteNoise.setLast_time(playDuration);
        whiteNoise.setEmotion(emotion);
        whiteNoise.setAudio_name(audioName);
        whiteNoise.setStarttime(now);

        whiteNoiseRepository.save(whiteNoise);  // 插入数据到数据库
    }
}

