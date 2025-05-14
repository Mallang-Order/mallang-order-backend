package com.mallang.mallnagorder.admin.service;

import com.mallang.mallnagorder.admin.service.MailService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

@Service
public class EmailAuthService {

    private final RedisTemplate<String, String> redisTemplate;
    private final MailService mailService;

    // 인증번호 유효 기간 (3분)
    private static final long AUTH_EXPIRATION_TIME = 3L; // minutes

    // 인증 완료 상태 유효 시간 (예: 10분)
    private static final long VERIFIED_EXPIRATION_TIME = 10L; // minutes

    public EmailAuthService(@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate, MailService mailService) {
        this.redisTemplate = redisTemplate;
        this.mailService = mailService;
    }

    public String sendAuthNumber(String email) throws MessagingException, UnsupportedEncodingException {
        String authNumber = generateAuthNumber();
        redisTemplate.opsForValue().set(email, authNumber, AUTH_EXPIRATION_TIME, TimeUnit.MINUTES);
        mailService.sendMail(email, authNumber);
        return authNumber;
    }

    public boolean validateAuthNumber(String email, String authNumber) {
        String storedAuthNumber = redisTemplate.opsForValue().get(email);

        if (storedAuthNumber == null || !storedAuthNumber.equals(authNumber)) {
            return false;
        }

        // 인증번호 삭제
        redisTemplate.delete(email);

        // 인증 완료 상태 저장 (예: 10분 유효)
        redisTemplate.opsForValue().set(getVerifiedKey(email), "true", VERIFIED_EXPIRATION_TIME, TimeUnit.MINUTES);

        return true;
    }

    public boolean isEmailVerified(String email) {
        String verified = redisTemplate.opsForValue().get(getVerifiedKey(email));
        return "true".equals(verified);
    }

    // 회원가입 완료 후 인증 상태 제거 (선택)
    public void removeVerifiedStatus(String email) {
        redisTemplate.delete(getVerifiedKey(email));
    }

    private String generateAuthNumber() {
        int authNum = (int) (Math.random() * 1000000);
        return String.format("%06d", authNum);
    }

    private String getVerifiedKey(String email) {
        return "verified:" + email;
    }
}

