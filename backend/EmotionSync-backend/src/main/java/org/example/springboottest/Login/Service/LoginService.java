package org.example.springboottest.Login.Service;

import org.example.springboottest.Login.Controller.LoginRepository;
import org.example.springboottest.User.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 寻找用户是否存在
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
    // 寻找用户名是否存在
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
    // 返回用户名和邮箱是否已经存在
    public boolean existsByEmailAndUsername(String email, String username) {
        return loginRepository.existsByEmailAndUsername(email, username);
    }
    // 修改密码
    public boolean resetPassword(String username, String email, String newPassword) {
        try {
            Optional<User> userOptional = loginRepository.findByEmailAndUsername(email, username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                // 密码进行加密
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
