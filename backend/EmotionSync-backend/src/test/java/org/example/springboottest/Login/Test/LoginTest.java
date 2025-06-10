package org.example.springboottest.Login.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springboottest.User.Entity.User;
import org.example.springboottest.Login.Controller.LoginRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private LoginRepository loginRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    public void setUp() {
//        loginRepository.deleteAll(); // 清空数据库
//        User user = new User();
//        user.setUsername("testuser");
//        user.setPassword(passwordEncoder.encode("password123"));
//        user.setEmail("test@example.com");
//        user.setIdentity("user");
//        loginRepository.save(user);
//    }
//
//    @Test
//    public void testLoginSuccess() throws Exception {
//        Map<String, String> request = Map.of(
//                "username", "testuser",
//                "password", "password123"
//        );
//
//        mockMvc.perform(post("/myHello/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.token").exists());
//    }
//
//    @Test
//    public void testLoginFailure() throws Exception {
//        Map<String, String> request = Map.of(
//                "username", "testuser",
//                "password", "wrongpass"
//        );
//
//        mockMvc.perform(post("/myHello/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.success").value(false));
//    }
//
//    @Test
//    public void testRegisterSuccess() throws Exception {
//        Map<String, String> request = Map.of(
//                "username", "newuser",
//                "password", "newpass123",
//                "email", "new@example.com"
//        );
//
//        mockMvc.perform(post("/myHello/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.token").exists());
//    }
//
//    @Test
//    public void testRegisterUsernameExists() throws Exception {
//        Map<String, String> request = Map.of(
//                "username", "testuser",
//                "password", "anotherpass",
//                "email", "dup@example.com"
//        );
//
//        mockMvc.perform(post("/myHello/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false));
//    }
//
//    @Test
//    public void testResetPasswordSuccess() throws Exception {
//        Map<String, String> request = Map.of(
//                "username", "testuser",
//                "email", "test@example.com",
//                "password", "newpassword"
//        );
//
//        mockMvc.perform(post("/myHello/reset-password")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true));
//    }
//
//    @Test
//    public void testResetPasswordFailure_UserNotFound() throws Exception {
//        Map<String, String> request = Map.of(
//                "username", "notexist",
//                "email", "notexist@example.com",
//                "password", "newpassword"
//        );
//
//        mockMvc.perform(post("/myHello/reset-password")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.success").value(false));
//    }
}
