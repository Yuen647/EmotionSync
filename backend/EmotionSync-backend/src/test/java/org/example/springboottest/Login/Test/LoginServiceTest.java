package org.example.springboottest.Login.Test;


import org.example.springboottest.Login.Controller.LoginController;
import org.example.springboottest.Login.Controller.LoginRepository;
import org.example.springboottest.Login.Service.LoginService;
import org.example.springboottest.User.Entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class LoginServiceTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------- findUser ----------
    @Test
    void testFindUser_Success() {
        User mockUser = new User();
        mockUser.setUsername("alice");
        mockUser.setPassword("encoded123");

        when(loginRepository.findByUsername("alice")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("raw123", "encoded123")).thenReturn(true);

        Optional<User> result = loginService.findUser("alice", "raw123");
        assertTrue(result.isPresent());
        assertEquals("alice", result.get().getUsername());
    }

    @Test
    void testFindUser_PasswordMismatch() {
        User mockUser = new User();
        mockUser.setUsername("alice");
        mockUser.setPassword("encoded123");

        when(loginRepository.findByUsername("alice")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongPass", "encoded123")).thenReturn(false);

        Optional<User> result = loginService.findUser("alice", "wrongPass");
        assertFalse(result.isPresent());
    }

    @Test
    void testFindUser_UserNotFound() {
        when(loginRepository.findByUsername("bob")).thenReturn(Optional.empty());

        Optional<User> result = loginService.findUser("bob", "anyPassword");
        assertFalse(result.isPresent());
    }

    // ---------- existsByUsername ----------
    @Test
    void testExistsByUsername_True() {
        when(loginRepository.existsByUsername("john")).thenReturn(true);
        assertTrue(loginService.existsByUsername("john"));
    }

    @Test
    void testExistsByUsername_False() {
        when(loginRepository.existsByUsername("no_user")).thenReturn(false);
        assertFalse(loginService.existsByUsername("no_user"));
    }

    // ---------- registerUser ----------
    @Test
    void testRegisterUser_Success() {
        when(passwordEncoder.encode("123456")).thenReturn("encrypted123");
        when(loginRepository.save(any(User.class))).thenReturn(null); // void return

        boolean result = loginService.registerUser("newUser", "123456", "new@email.com");
        assertTrue(result);
    }

    @Test
    void testRegisterUser_ExceptionThrown() {
        when(passwordEncoder.encode(anyString())).thenReturn("encrypted");
        doThrow(new RuntimeException("DB error")).when(loginRepository).save(any(User.class));

        boolean result = loginService.registerUser("errUser", "123", "err@email.com");
        assertFalse(result);
    }

    // ---------- existsByEmailAndUsername ----------
    @Test
    void testExistsByEmailAndUsername_True() {
        when(loginRepository.existsByEmailAndUsername("a@a.com", "user")).thenReturn(true);
        assertTrue(loginService.existsByEmailAndUsername("a@a.com", "user"));
    }

    @Test
    void testExistsByEmailAndUsername_False() {
        when(loginRepository.existsByEmailAndUsername("b@b.com", "no_user")).thenReturn(false);
        assertFalse(loginService.existsByEmailAndUsername("b@b.com", "no_user"));
    }

    // ---------- resetPassword ----------
    @Test
    void testResetPassword_Success() {
        User user = new User();
        user.setUsername("resetUser");
        user.setEmail("reset@email.com");

        when(loginRepository.findByEmailAndUsername("reset@email.com", "resetUser"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNew");
        when(loginRepository.save(any(User.class))).thenReturn(null);

        boolean result = loginService.resetPassword("resetUser", "reset@email.com", "newPass");
        assertTrue(result);
    }

    @Test
    void testResetPassword_UserNotFound() {
        when(loginRepository.findByEmailAndUsername("none@email.com", "noneUser"))
                .thenReturn(Optional.empty());

        boolean result = loginService.resetPassword("noneUser", "none@email.com", "pass");
        assertFalse(result);
    }

    @Test
    void testResetPassword_ExceptionThrown() {
        User user = new User();
        user.setUsername("user");
        user.setEmail("email");

        when(loginRepository.findByEmailAndUsername("email", "user")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        doThrow(new RuntimeException("DB error")).when(loginRepository).save(any(User.class));

        boolean result = loginService.resetPassword("user", "email", "pass");
        assertFalse(result);
    }
}

