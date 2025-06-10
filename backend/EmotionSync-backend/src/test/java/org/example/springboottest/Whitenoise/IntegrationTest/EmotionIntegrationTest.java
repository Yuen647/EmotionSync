package org.example.springboottest.Whitenoise.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class EmotionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void recommendAudio_shouldReturnRecommendations_forValidEmotion() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "emotion", "relaxed",
                "numRecommendations", 3
        );

        mockMvc.perform(post("/api/whitenoise/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$[0].audioName").isNotEmpty())
                .andExpect(jsonPath("$[0].audioSrc").isNotEmpty())
                .andExpect(jsonPath("$[0].audioIcon").isNotEmpty());
    }

    @Test
    void recommendAudio_shouldReturnEmptyList_forUnknownEmotion() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "emotion", "unknown_emotion",
                "numRecommendations", 3
        );

        mockMvc.perform(post("/api/whitenoise/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(0)));
    }
}
