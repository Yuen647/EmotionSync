package org.example.springboottest.Whitenoise.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springboottest.Whitenoise.Entity.Audio;
import org.example.springboottest.Whitenoise.Service.EmotionRecommender;
import org.example.springboottest.config.JwtFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmotionController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
})

@AutoConfigureMockMvc(addFilters = false)
class EmotionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 添加对服务的模拟
    @MockBean
    private EmotionRecommender recommenderService;

    @Test
    void recommendAudio_success() throws Exception {
        List<Audio> dummyAudios = List.of(
                new Audio("Rain", "rain.mp3", "rain.svg"),
                new Audio("Waves", "waves.mp3", "waves.svg")
        );

        when(recommenderService.recommend(Map.of("relaxed", 1.0), 2))
                .thenReturn(dummyAudios);

        Map<String, Object> body = Map.of(
                "emotion", "relaxed",
                "numRecommendations", 2
        );

        mockMvc.perform(post("/api/whitenoise/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].audioName").value("Rain"))
                .andExpect(jsonPath("$[0].audioSrc").value("rain.mp3"))
                .andExpect(jsonPath("$[0].audioIcon").value("rain.svg"))
                .andExpect(jsonPath("$[1].audioName").value("Waves"))
                .andExpect(jsonPath("$[1].audioSrc").value("waves.mp3"))
                .andExpect(jsonPath("$[1].audioIcon").value("waves.svg"));
    }

    @Test
    void recommendAudio_unknownEmotion_returnsEmpty() throws Exception {
        when(recommenderService.recommend(Map.of("unknown", 1.0), 1))
                .thenReturn(List.of());

        Map<String, Object> body = Map.of(
                "emotion", "unknown",
                "numRecommendations", 1
        );

        mockMvc.perform(post("/api/whitenoise/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
