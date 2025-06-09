package org.example.springboottest.Whitenoise.Service;

import org.example.springboottest.User.Controller.UserRepository;
import org.example.springboottest.User.Entity.User;
import org.example.springboottest.Whitenoise.Controller.AudioRepository;
import org.example.springboottest.Whitenoise.Controller.WhiteNoiseRepository;
import org.example.springboottest.Whitenoise.Entity.Audio;
import org.example.springboottest.Whitenoise.Entity.WhiteNoise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class WhiteNoiseServiceTest {

    private WhiteNoiseRepository whiteNoiseRepository;
    private UserRepository userRepository;
    private AudioRepository audioRepository;
    private WhiteNoiseService whiteNoiseService;

    @BeforeEach
    void setUp() {
        whiteNoiseRepository = mock(WhiteNoiseRepository.class);
        userRepository = mock(UserRepository.class);
        audioRepository = mock(AudioRepository.class);
        whiteNoiseService = new WhiteNoiseService(whiteNoiseRepository, userRepository, audioRepository);
    }

    @Test
    void saveWhiteNoise_success() {
        // 准备数据
        User user = new User();
        user.setUserId(1L);
        when(userRepository.findByUsername("john")).thenReturn(user);

        Audio audio = new Audio();
        audio.setAudioName("Rain");
        when(audioRepository.findByAudioName("Rain")).thenReturn(audio);

        // 调用方法
        LocalDateTime now = LocalDateTime.now();
        whiteNoiseService.saveWhiteNoise("john", 120, "calm", "Rain", now);

        // 验证行为
        verify(whiteNoiseRepository, times(1)).save(any(WhiteNoise.class));
    }

    @Test
    void saveWhiteNoise_userNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                whiteNoiseService.saveWhiteNoise("unknown", 120, "calm", "Rain", LocalDateTime.now())
        );

        assertTrue(ex.getMessage().contains("User not found"));
        verify(whiteNoiseRepository, never()).save(any());
    }

    @Test
    void saveWhiteNoise_audioNotFound() {
        User user = new User();
        user.setUserId(1L);
        when(userRepository.findByUsername("john")).thenReturn(user);
        when(audioRepository.findByAudioName("Unknown")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                whiteNoiseService.saveWhiteNoise("john", 120, "calm", "Unknown", LocalDateTime.now())
        );

        assertTrue(ex.getMessage().contains("Audio not found"));
        verify(whiteNoiseRepository, never()).save(any());
    }
}
