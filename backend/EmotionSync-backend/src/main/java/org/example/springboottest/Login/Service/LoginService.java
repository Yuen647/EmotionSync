package org.example.springboottest.Login.Service;

import org.example.springboottest.Login.Controller.LoginRepository;
import org.example.springboottest.User.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class LoginService {
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findUser(String username, String password) {
        Optional<User> userOptional = loginRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 匹配密码（明文 vs 加密）
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public boolean existsByUsername(String username) {
        return loginRepository.existsByUsername(username);
    }
    // 注册用户
    public boolean registerUser(String username, String password, String email) {
        try {
            User user = new User();
            user.setUsername(username);
            String encryptedPassword = passwordEncoder.encode(password);
            user.setPassword(encryptedPassword);
            user.setEmail(email);
            user.setIdentity("user");
            loginRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean existsByEmailAndUsername(String email, String username) {
        return loginRepository.existsByEmailAndUsername(email, username);
    }

    public boolean resetPassword(String username, String email, String newPassword) {
        try {
            Optional<User> userOptional = loginRepository.findByEmailAndUsername(email, username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                loginRepository.save(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
