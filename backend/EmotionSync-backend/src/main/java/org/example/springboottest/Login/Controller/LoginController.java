package org.example.springboottest.Login.Controller;

import org.example.springboottest.Login.Service.LoginService;
import org.example.springboottest.User.Entity.User;
import org.example.springboottest.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("myHello")
public class LoginController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> requestData) {
        String username = requestData.get("username");
        String password = requestData.get("password");

        // 检查请求数据是否完整
        if (Objects.equals(username, "") || Objects.equals(password, "")) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "信息不完整"));
        }
        // 数据库中寻找用户
        Optional<User> user = loginService.findUser(username, password);

        if (user.isPresent()) {
            // 生成 JWT 令牌
            String token = jwtUtil.generateToken(username);
            System.out.println(token);
            return ResponseEntity.ok(Map.of("success", true, "message", "登录成功", "token", token));
        } else {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户名或密码错误"));
        }

    }


    // 注册接口
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> requestData) {
        String email = requestData.get("email");
        String username = requestData.get("username");
        String password = requestData.get("password");

        // 检查请求数据是否完整
        if (Objects.equals(email, "") || Objects.equals(username, "") || Objects.equals(password, "")) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "信息不完整"));
        }
        // 密码长度小于6
        if(password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "密码长度输入错误"));
        }
        // 用户是否已经存在
        if(loginService.existsByUsername(username)){
            return ResponseEntity.status(400).body(Map.of("success", false, "message", "用户名已存在"));
        }
        // 检查用户是否注册成功
        boolean user = loginService.registerUser(username, password, email);

        if (user) {
            // 生成 JWT 令牌
            String token = jwtUtil.generateToken(username);
            return ResponseEntity.ok(Map.of("success", true, "message", "注册成功", "token", token));
        } else {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "注册失败"));
        }
    }
    // 修改密码部分
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> requestData) {
        String email = requestData.get("email");
        String username = requestData.get("username");
        String newPassword = requestData.get("password");

        // 检查请求数据是否完整
        if (Objects.equals(email, "") || Objects.equals(username, "") || Objects.equals(newPassword, "")) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "信息不完整"));
        }
        // 新密码设置长度小于6
        if(newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "密码长度输入错误"));
        }
        // 检查用户是否存在
        if (!loginService.existsByEmailAndUsername(email, username)) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "用户不存在"));
        }

        // 重置密码
        boolean user = loginService.resetPassword(username, email, newPassword);

        if (user) {
            // 生成 JWT 令牌
            String token = jwtUtil.generateToken(username);
            return ResponseEntity.ok(Map.of("success", true, "message", "密码重置成功","token", token));
        } else {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "密码重置失败"));
        }
    }


}
