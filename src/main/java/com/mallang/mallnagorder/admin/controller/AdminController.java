package com.mallang.mallnagorder.admin.controller;

import com.mallang.mallnagorder.admin.dto.*;
import com.mallang.mallnagorder.admin.exception.AdminException;
import com.mallang.mallnagorder.admin.service.AdminService;
import com.mallang.mallnagorder.admin.service.EmailAuthService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final EmailAuthService emailAuthService;

    public AdminController(AdminService adminService, EmailAuthService emailAuthService) {
        this.adminService = adminService;
        this.emailAuthService = emailAuthService;
    }

    @PostMapping("/emailSend")
    public ResponseEntity<String> emailSend(@RequestBody EmailCheckRequest request) throws MessagingException, UnsupportedEncodingException {
        adminService.emailValidate(request);
        String authNum = emailAuthService.sendAuthNumber(request.getEmail());
        log.info("이메일 인증번호 발송: {}", authNum);
        return new ResponseEntity<>("이메일 인증번호가 발송되었습니다. 인증을 진행해주세요.", HttpStatus.OK);
    }

    @PostMapping("/emailCheck")
    public ResponseEntity<EmailCheckResponse> emailCheck(@RequestBody EmailCheckRequest request) {
        log.info("이메일 인증 요청 - 이메일: {}, 인증번호: {}", request.getEmail(), request.getAuthNum());

        boolean isValid = emailAuthService.validateAuthNumber(request.getEmail(), request.getAuthNum());

        if (isValid) {
            return new ResponseEntity<>(new EmailCheckResponse(true, "이메일 인증 성공"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new EmailCheckResponse(false, "인증번호가 잘못되었습니다."), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/join")
    public ResponseEntity<Long> addMember(@RequestBody JoinRequest request) {
        Long savedAdminId = adminService.join(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdminId);
    }

    @PostMapping("/changeName")
    public ResponseEntity<CheckResponse> changeName(@AuthenticationPrincipal AdminDetails adminDetails,
                                                    @RequestBody ChangeNameRequest request) {
        String email = adminDetails.getUsername();
        CheckResponse response = adminService.changeName(email, request.getNewName());
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<CheckResponse> changePassword(@AuthenticationPrincipal AdminDetails adminDetails,
                                                        @RequestBody ChangePasswordRequest request) {
        String email = adminDetails.getUsername();
        try {
            CheckResponse response = adminService.changePassword(email, request.getOldPassword(), request.getNewPassword());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AdminException e) {
            return new ResponseEntity<>(new CheckResponse(false, e.getMessage()), e.getExceptionType().getHttpStatus());
        }
    }

    @PutMapping("/deleteMember")
    public ResponseEntity<CheckResponse> deleteMember(@AuthenticationPrincipal AdminDetails adminDetails,
                                                      @RequestBody DeleteAdminRequest request) {
        String email = adminDetails.getUsername();
        try {
            CheckResponse response = adminService.deleteAdmin(email, request.getPassword());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AdminException e) {
            return new ResponseEntity<>(new CheckResponse(false, e.getMessage()), e.getExceptionType().getHttpStatus());
        }
    }
}
