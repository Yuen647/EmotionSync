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

    /**
     * 根据用户名和密码查找并验证用户。
     *
     * @param username 用户名
     * @param password 明文密码（用户输入）
     * @return 若用户名存在且密码匹配，返回对应的用户对象；否则返回 Optional.empty()
     */
    public Optional<User> findUser(String username, String password) {
        Optional<User> userOptional = loginRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 匹配密码（明文 vs 加密）
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user); // 验证通过，返回用户对象
            }
        }
        return Optional.empty(); // 用户不存在或密码不匹配，返回空对象
    }
    /**
     * 检查用户名是否已存在。
     *
     * @param username 待检查的用户名
     * @return 若用户名存在则返回 true，否则返回 false
     */
    public boolean existsByUsername(String username) {
        return loginRepository.existsByUsername(username);
    }
    /**
     * 注册新用户（设置用户名、加密后的密码、邮箱及默认身份）。
     *
     * @param username 用户名
     * @param password 明文密码
     * @param email    邮箱地址
     * @return 注册成功返回 true，失败返回 false
     */
    public boolean registerUser(String username, String password, String email) {
        try {
            User user = new User(); // 构造用户对象
            user.setUsername(username);
            // 对密码进行加密后存储
            String encryptedPassword = passwordEncoder.encode(password);
            user.setPassword(encryptedPassword);
            user.setEmail(email);
            user.setIdentity("user");// 默认身份为普通用户
            loginRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 检查给定用户名和邮箱组合是否已存在。
     *
     * @param email    邮箱地址
     * @param username 用户名
     * @return 若该组合已存在，则返回 true；否则返回 false
     */
    public boolean existsByEmailAndUsername(String email, String username) {
        return loginRepository.existsByEmailAndUsername(email, username);
    }
    /**
     * 重置用户密码（需提供正确的用户名和邮箱验证）。
     *
     * @param username    用户名
     * @param email       邮箱地址
     * @param newPassword 新的明文密码
     * @return 修改成功返回 true，失败（如用户不存在）返回 false
     */
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
            return false; // 用户不存在
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
