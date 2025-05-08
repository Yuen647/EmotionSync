package org.example.springboottest.Login.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springboottest.Login.Service.LoginService;
import org.example.springboottest.User.Entity.User;
import org.example.springboottest.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)  // 关闭 Spring Security 过滤器
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;

    @MockBean
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void login_success() throws Exception {
        Mockito.when(loginService.findUser("testuser", "123456"))
                .thenReturn(Optional.of(new User()));
        Mockito.when(jwtUtil.generateToken("testuser")).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/myHello/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("username", "testuser", "password", "123456")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    void register_success() throws Exception {
        Mockito.when(loginService.existsByUsername("newuser")).thenReturn(false);
        Mockito.when(loginService.registerUser("newuser", "abcdef", "test@example.com")).thenReturn(true);
        Mockito.when(jwtUtil.generateToken("newuser")).thenReturn("reg-jwt");

        mockMvc.perform(post("/myHello/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("email", "test@example.com", "username", "newuser", "password", "abcdef")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("reg-jwt"));
    }

    @Test
    void resetPassword_success() throws Exception {
        Mockito.when(loginService.existsByEmailAndUsername("test@example.com", "existinguser")).thenReturn(true);
        Mockito.when(loginService.resetPassword("existinguser", "test@example.com", "newpass")).thenReturn(true);
        Mockito.when(jwtUtil.generateToken("existinguser")).thenReturn("reset-jwt");

        mockMvc.perform(post("/myHello/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("email", "test@example.com", "username", "existinguser", "password", "newpass")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("reset-jwt"));
    }
}
