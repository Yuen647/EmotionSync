package org.example.springboottest.Whitenoise.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springboottest.User.Entity.User;
import org.example.springboottest.Whitenoise.Entity.Audio;
import org.example.springboottest.Whitenoise.Controller.AudioRepository;
import org.example.springboottest.Whitenoise.Controller.WhiteNoiseRepository;
import org.example.springboottest.User.Controller.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class WhiteNoiseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AudioRepository audioRepository;

    @MockBean
    private WhiteNoiseRepository whiteNoiseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        // 默认行为可以设置在此处
    }

    @Test
    void saveWhiteNoise_success() throws Exception {
        User mockUser = new User();
        mockUser.setUserId(1L);
        Audio mockAudio = new Audio();
        mockAudio.setAudioName("Rain");

        when(userRepository.findByUsername("john")).thenReturn(mockUser);
        when(audioRepository.findByAudioName("Rain")).thenReturn(mockAudio);

        Map<String, Object> request = Map.of(
                "username", "john",
                "playDuration", 120,
                "emotion", "calm",
                "audioName", "Rain"
        );

        mockMvc.perform(post("/api/whitenoise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("插入成功"));

        verify(whiteNoiseRepository, times(1)).save(any());
    }

    @Test
    void saveWhiteNoise_userNotFound() throws Exception {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        Map<String, Object> request = Map.of(
                "username", "unknown",
                "playDuration", 100,
                "emotion", "sad",
                "audioName", "Rain"
        );

        mockMvc.perform(post("/api/whitenoise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("数据库错误，请稍后再试"));

        verify(whiteNoiseRepository, never()).save(any());
    }

    @Test
    void saveWhiteNoise_audioNotFound() throws Exception {
        User mockUser = new User();
        mockUser.setUserId(2L);

        when(userRepository.findByUsername("john")).thenReturn(mockUser);
        when(audioRepository.findByAudioName("Unknown")).thenReturn(null);

        Map<String, Object> request = Map.of(
                "username", "john",
                "playDuration", 90,
                "emotion", "angry",
                "audioName", "Unknown"
        );

        mockMvc.perform(post("/api/whitenoise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("数据库错误，请稍后再试"));

        verify(whiteNoiseRepository, never()).save(any());
    }
}
