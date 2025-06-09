package org.example.springboottest.Whitenoise.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springboottest.Whitenoise.Service.WhiteNoiseService;
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

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WhiteNoiseController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
})

@AutoConfigureMockMvc(addFilters = false)
class WhiteNoiseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WhiteNoiseService whiteNoiseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testReceiveWhiteNoiseData_success() throws Exception {
        Map<String, Object> requestData = Map.of(
                "username", "testuser",
                "playDuration", 300,
                "emotion", "relaxed",
                "audioName", "Rain"
        );

        // 不抛异常即表示成功
        doNothing().when(whiteNoiseService).saveWhiteNoise(
                eq("testuser"),
                eq(300),
                eq("relaxed"),
                eq("Rain"),
                any() // LocalDateTime now
        );

        mockMvc.perform(post("/api/whitenoise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("插入成功"));
    }

    @Test
    void testReceiveWhiteNoiseData_failure() throws Exception {
        Map<String, Object> requestData = Map.of(
                "username", "testuser",
                "playDuration", 300,
                "emotion", "relaxed",
                "audioName", "Rain"
        );

        doThrow(new RuntimeException("DB Error")).when(whiteNoiseService)
                .saveWhiteNoise(anyString(), anyInt(), anyString(), anyString(), any());

        mockMvc.perform(post("/api/whitenoise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestData)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("数据库错误，请稍后再试"));
    }
}
