package org.example.springboottest.Whitenoise.Service;

import org.example.springboottest.User.Controller.UserRepository;
import org.example.springboottest.User.Controller.UserService;
import org.example.springboottest.User.Entity.User;
import org.example.springboottest.Whitenoise.Controller.WhiteNoiseRepository;
import org.example.springboottest.Whitenoise.Entity.WhiteNoise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WhiteNoiseService {

    @Autowired
    private WhiteNoiseRepository whiteNoiseRepository;
    @Autowired
    private UserRepository userRepository;

    public void saveWhiteNoise(String username, int playDuration, String emotion, String audioName, LocalDateTime now) {
        User user = userRepository.findByUsername(username);
        System.out.println(user.getUserId());
        WhiteNoise whiteNoise = new WhiteNoise();
        whiteNoise.setUserId(user.getUserId());
        whiteNoise.setLastTime(playDuration);
        whiteNoise.setEmotion(emotion);
        whiteNoise.setAudioName(audioName);
        whiteNoise.setStartTime(now);

        whiteNoiseRepository.save(whiteNoise);  // 插入数据到数据库
    }
}

