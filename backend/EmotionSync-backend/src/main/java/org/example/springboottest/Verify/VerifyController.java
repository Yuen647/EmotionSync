package org.example.springboottest.Verify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/verify")
public class VerifyController {

    @Autowired
    private VerifyService verifyService;

    @PostMapping("/send")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        boolean success = verifyService.sendVerificationCode(email);
        if (success) {
            return ResponseEntity.ok(Map.of("code", 200, "message", "验证码已发送"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "验证码发送失败"));
        }
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");
        boolean isValid = verifyService.verifyCode(email, code);
        if (isValid) {
            return ResponseEntity.ok(Map.of("code", 200, "message", "验证码正确"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "验证码错误"));
        }
    }
}

