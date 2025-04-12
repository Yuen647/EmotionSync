package org.example.springboottest.Verify;

import jakarta.annotation.Resource;
import org.example.springboottest.util.EmailApi;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerifyService {
    @Resource
    private EmailApi emailApi;

    private final Map<String, VerificationCode> codeMap = new ConcurrentHashMap<>();
    private static final long EXPIRATION_TIME = 5 * 60 * 1000;  // 设置验证码有效期为5分钟（单位：毫秒）

    public boolean sendVerificationCode(String email) {
        String code = String.valueOf(new Random().nextInt(900000) + 100000); // 6位验证码
        long currentTime = System.currentTimeMillis();
        codeMap.put(email, new VerificationCode(code, currentTime));
        emailApi.sendHtmlEmail("请接收验证码", code, email);
        return true;
    }

    public boolean verifyCode(String email, String code) {
        VerificationCode verificationCode = codeMap.get(email);
        if (verificationCode == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        // 验证码是否过期
        if (currentTime - verificationCode.getTimestamp() > EXPIRATION_TIME) {
            codeMap.remove(email);  // 移除过期验证码
            return false;
        }
        return code.equals(verificationCode.getCode());
    }

    // 内部类用于存储验证码和生成时间
    private static class VerificationCode {
        private String code;
        private long timestamp;

        public VerificationCode(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }

        public String getCode() {
            return code;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
