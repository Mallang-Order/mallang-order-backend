package com.mallang.mallnagorder.admin.controller;

import com.mallang.mallnagorder.admin.dto.*;
import com.mallang.mallnagorder.admin.exception.AdminException;
import com.mallang.mallnagorder.admin.exception.AdminExceptionType;
import com.mallang.mallnagorder.admin.service.AdminService;
import com.mallang.mallnagorder.admin.service.EmailAuthService;
import com.mallang.mallnagorder.auth.service.AuthService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final EmailAuthService emailAuthService;
    private final AuthService authService;

    public AdminController (AdminService adminService, EmailAuthService emailAuthService,  AuthService authService) {
        this.adminService = adminService;
        this.emailAuthService = emailAuthService;
        this.authService = authService;
    }

    @PostMapping("/emailSend")
    public ResponseEntity<String> emailSend(@RequestBody EmailCheckRequest request) throws MessagingException, UnsupportedEncodingException {

        // 이메일 유효성 확인
        adminService.emailValidate(request);
        // 이메일 인증을 위한 인증번호 발송
        String authNum = emailAuthService.sendAuthNumber(request.getEmail());

        log.info("이메일 인증번호 발송: {}", authNum);

        return new ResponseEntity<>("이메일 인증번호가 발송되었습니다. 인증을 진행해주세요.", HttpStatus.OK);
    }

    // 이메일 인증번호 확인
    @PostMapping("/emailCheck")
    public ResponseEntity<EmailCheckResponse> emailCheck(@RequestBody EmailCheckRequest request) {

        // 클라이언트에서 받은 이메일과 인증번호를 로그로 출력
        log.info("이메일 인증 요청 - 이메일: {}, 인증번호: {}", request.getEmail(), request.getAuthNum());

        boolean isValid = emailAuthService.validateAuthNumber(request.getEmail(), request.getAuthNum());

        if (isValid) {
            EmailCheckResponse response = new EmailCheckResponse(true, "이메일 인증 성공");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            EmailCheckResponse response = new EmailCheckResponse(false, "인증번호를 다시 확인해주세요.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/join")
    public ResponseEntity<Long> addMember(@RequestBody JoinRequest request) {

        if (!emailAuthService.isEmailVerified(request.getEmail())) {
            throw new AdminException(AdminExceptionType.WRONG_EMAIL_AUTHCODE);
        }

        // 회원가입 성공 시 인증 상태 제거 (선택)
        Long savedAdminId = adminService.join(request);
        emailAuthService.removeVerifiedStatus(request.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedAdminId);
    }

    // 이름 변경 API
    @PostMapping("/changeName")
    public ResponseEntity<CheckResponse> changeName(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                    @RequestBody ChangeNameRequest request) {

        // Authorization header 에서 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);

        // MemberService 에서 이름 변경 처리
        CheckResponse response = adminService.changeName(email, request.getNewName());

        // 처리 결과에 따라 적절한 응답 반환
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // 비밀번호 변경 API
    @PostMapping("/changePassword")
    public ResponseEntity<CheckResponse> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                        @RequestBody ChangePasswordRequest request) {
        // Authorization header 에서 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);

        try {
            // MemberService 에서 비밀번호 변경 처리
            CheckResponse response = adminService.changePassword(email, request.getOldPassword(), request.getNewPassword());

            // 비밀번호 변경 성공
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (AdminException e) {
            // 예외가 발생하면 전역 예외 처리기로 넘김
            CheckResponse errorResponse = new CheckResponse(false, e.getMessage());
            return new ResponseEntity<>(errorResponse, e.getExceptionType().getHttpStatus());
        }
    }

    // 관리자 삭제 API
    @PutMapping("/deleteMember")
    public ResponseEntity<CheckResponse> deleteMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @RequestBody DeleteAdminRequest request) {
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);
        try {
            // 서비스 레이어에서 회원 삭제
            CheckResponse response = adminService.deleteAdmin(email, request.getPassword());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AdminException e) {
            // 전역 예외 처리기로 예외를 처리
            return new ResponseEntity<>(new CheckResponse(false, e.getMessage()), e.getExceptionType().getHttpStatus());
        }
    }
}