package org.example.springboottest.Login.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springboottest.Login.Controller.LoginController;
import org.example.springboottest.Login.Service.LoginService;
import org.example.springboottest.User.Entity.User;
import org.example.springboottest.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;

    @MockBean
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("登录成功测试")
    void loginSuccessTest() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("testUser");

        Mockito.when(loginService.findUser("testUser", "password123"))
                .thenReturn(Optional.of(mockUser));
        Mockito.when(jwtUtil.generateToken("testUser")).thenReturn("mockToken");

        mockMvc.perform(post("/myHello/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "testUser",
                                "password", "password123"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("mockToken"));
    }

    // 登录失败测试（用户不存在或密码错误）
    @Test
    @DisplayName("登录失败测试")
    void loginFailureTest() throws Exception {
        Mockito.when(loginService.findUser("wrongUser", "wrongPass"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/myHello/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "wrongUser",
                                "password", "wrongPass"
                        ))))
                .andExpect(status().isUnauthorized());  // 401 Unauthorized
    }

    @Test
    @DisplayName("注册成功测试")
    void registerSuccessTest() throws Exception {
        Mockito.when(loginService.existsByUsername("newUser")).thenReturn(false);
        Mockito.when(loginService.registerUser("newUser", "password123", "user@example.com"))
                .thenReturn(true);
        Mockito.when(jwtUtil.generateToken("newUser")).thenReturn("mockToken");

        mockMvc.perform(post("/myHello/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "newUser",
                                "password", "password123",
                                "email", "user@example.com"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.token").value("mockToken"));
    }

    // 注册失败测试（用户名已存在）
    @Test
    @DisplayName("注册失败测试")
    void registerFailureTest() throws Exception {
        Mockito.when(loginService.existsByUsername("existingUser")).thenReturn(true);

        mockMvc.perform(post("/myHello/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "existingUser",
                                "password", "password123",
                                "email", "user@example.com"
                        ))))
                .andExpect(status().isBadRequest());  // 400 Bad Request
    }

    @Test
    @DisplayName("重置密码成功测试")
    void resetPasswordSuccessTest() throws Exception {
        Mockito.when(loginService.existsByEmailAndUsername("user@example.com", "testUser"))
                .thenReturn(true);
        Mockito.when(loginService.resetPassword("testUser", "user@example.com", "newPass123"))
                .thenReturn(true);
        Mockito.when(jwtUtil.generateToken("testUser")).thenReturn("mockToken");

        mockMvc.perform(post("/myHello/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "testUser",
                                "email", "user@example.com",
                                "password", "newPass123"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("密码重置成功"))
                .andExpect(jsonPath("$.token").value("mockToken"));
    }
    // 重置密码失败测试（邮箱或用户名不匹配）
    @Test
    @DisplayName("重置密码失败测试")
    void resetPasswordFailureTest() throws Exception {
        Mockito.when(loginService.existsByEmailAndUsername("wrong@example.com", "testUser"))
                .thenReturn(false);

        mockMvc.perform(post("/myHello/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "testUser",
                                "email", "wrong@example.com",
                                "password", "newPass123"
                        ))))
                .andExpect(status().isNotFound());  // 404 Not Found
    }
}
