package org.example.springboottest.Verify;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerifyService {

    private final Map<String, String> codeMap = new ConcurrentHashMap<>();

    public boolean sendVerificationCode(String email) {
        String code = String.valueOf(new Random().nextInt(900000) + 100000); // 6位验证码
        codeMap.put(email, code);
        System.out.println("向 " + email + " 发送验证码：" + code);
        // TODO: 实际中你应该在这里发邮件，或用 MailService
        return true;
    }

    public boolean verifyCode(String email, String code) {
        return code.equals(codeMap.getOrDefault(email, ""));
    }
}

