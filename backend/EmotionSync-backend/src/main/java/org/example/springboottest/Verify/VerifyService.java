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

    private final Map<String, String> codeMap = new ConcurrentHashMap<>();

    public boolean sendVerificationCode(String email) {
        String code = String.valueOf(new Random().nextInt(900000) + 100000); // 6位验证码
        codeMap.put(email, code);
        emailApi.sendHtmlEmail("请接收验证码", code,email);
        return true;
    }

    public boolean verifyCode(String email, String code) {
        return code.equals(codeMap.getOrDefault(email, ""));
    }
}

