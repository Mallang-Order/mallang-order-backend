package com.mallang.mallnagorder.auth.service;


import com.mallang.mallnagorder.auth.exception.AuthException;
import com.mallang.mallnagorder.auth.exception.AuthExceptionType;
import com.mallang.mallnagorder.global.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final JWTUtil jwtUtil;

    public AuthService(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Authorization 헤더에서 토큰을 추출하는 메서드
    public String extractTokenFromAuthorizationHeader(String authorizationHeader) {

        log.debug("Authorization 헤더: {}", authorizationHeader);


        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("유효하지 않은 Authorization 헤더: {}", authorizationHeader);
            throw new AuthException(AuthExceptionType.INVALID_AUTHORIZATION_HEADER);
        }

        // Bearer 토큰에서 실제 토큰 추출
        String token = authorizationHeader.substring(7).trim();
        log.debug("추출된 토큰: {}", token);

        if (token.split("\\.").length != 3) {
            log.error("잘못된 JWT 토큰 형식입니다: {}", token);
            throw new AuthException(AuthExceptionType.INVALID_TOKEN);
        }

        return token;
    }

    public String extractEmailFromToken(String token) {
        // 공백 제거 및 형식 검증
        if (token == null || token.trim().isEmpty()) {
            log.error("토큰이 유효하지 않습니다: {}", token);
            throw new AuthException(AuthExceptionType.INVALID_TOKEN);
        }

        try {
            // 불법적인 제어 문자가 포함된 경우 처리
            token = token.replaceAll("[\\x00-\\x1F\\x7F]", "");  // ASCII 제어 문자 및 DEL 문자를 제거

            // JWT 형식 검증
            if (token.split("\\.").length != 3) {
                log.error("잘못된 JWT 형식입니다: {}", token);
                throw new AuthException(AuthExceptionType.INVALID_TOKEN);
            }

            // 토큰 만료 여부 확인
            if (jwtUtil.isExpired(token)) {
                log.error("토큰이 만료되었습니다: {}", token);
                throw new AuthException(AuthExceptionType.TOKEN_EXPIRED);
            }

            // 토큰에서 이메일 추출
            String email = jwtUtil.getUsername(token);
            log.debug("추출된 이메일: {}", email);

            log.debug("디코딩된 이메일: {}", email);

            return email;


        } catch (MalformedJwtException e) {
            log.error("잘못된 JWT 형식입니다: {}", token);
            throw new AuthException(AuthExceptionType.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다: {}", token);
            throw new AuthException(AuthExceptionType.TOKEN_EXPIRED);
        } catch (JwtException e) {
            log.error("JWT 처리 오류 발생: {}", e.getMessage());
            throw new AuthException(AuthExceptionType.UNAUTHORIZED_ACCESS);
        } catch (Exception e) {
            log.error("토큰에서 이메일을 추출하는 데 실패했습니다: {}", e.getMessage());
            throw new AuthException(AuthExceptionType.UNAUTHORIZED_ACCESS);
        }
    }

    // 최종적으로 Authorization 헤더에서 이메일을 추출하는 메서드
    public String extractEmailFromAuthorizationHeader(String authorizationHeader) {
        String token = extractTokenFromAuthorizationHeader(authorizationHeader);
        return extractEmailFromToken(token);
    }

}