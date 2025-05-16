package com.mallang.mallnagorder.admin.service;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.admin.dto.CheckResponse;
import com.mallang.mallnagorder.admin.dto.EmailCheckRequest;
import com.mallang.mallnagorder.admin.dto.JoinRequest;
import com.mallang.mallnagorder.admin.exception.AdminException;
import com.mallang.mallnagorder.admin.exception.AdminExceptionType;
import com.mallang.mallnagorder.admin.repository.AdminRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

//import static javax.swing.text.html.parser.DTDConstants.ID;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AdminService(AdminRepository adminRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.adminRepository = adminRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //이메일 체크
    public void emailValidate(EmailCheckRequest emailCheckRequest) throws AdminException {
        String email = emailCheckRequest.getEmail();
        // 이메일 중복 체크
        isExistEmail(email);
        // 이메일 형식 체크
        checkEmailValid(email);
    }

    public Long join(JoinRequest joinRequest) {
        String email = joinRequest.getEmail();
        String password = joinRequest.getPassword();
        String adminName = joinRequest.getAdminName();
        String storeName = joinRequest.getStoreName();

        // 비밀번호 형식 체크
        checkPasswordValid(password);

        // 상점 이름 중복 체크
        isExistName(storeName);

        Admin admin = new Admin();
        admin.setEmail(email);
        admin.setPassword(bCryptPasswordEncoder.encode(password)); // 비밀번호 암호화
        admin.setAdminName(adminName);
        admin.setStoreName(storeName);

        // 디버깅 로그 추가
        System.out.println("Saving admin: " + admin);

        // 회원 정보를 DB에 저장하고, 저장된 객체 반환
        Admin savedAdmin = adminRepository.save(admin);

        // 디버깅 로그 추가
        System.out.println("Saved Admin ID: " + savedAdmin.getId());

        return savedAdmin.getId();
    }


    // 상점 이름 변경
    public CheckResponse changeName(String email, String newName) throws AdminException {

        //이메일로 회원 찾기
         Admin admin = adminRepository.findByEmail(email)
                 .orElseThrow(()-> new AdminException(AdminExceptionType.ADMIN_NOT_EXIST));


        //상점 이름 중복 확인
        isExistName(newName);

        //새로운 이름으로 변경
        admin.setStoreName(newName);
        adminRepository.save(admin); // 변경된 이름을 DB에 저장

        return new CheckResponse(true, "이름이 성공적으로 변경되었습니다.");
    }

    //비밀번호 변경
    public CheckResponse changePassword(String email, String oldPassword, String newPassword) throws AdminException {

        //이메일로 회원 찾기
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(()-> new AdminException(AdminExceptionType.ADMIN_NOT_EXIST));


        // 기존 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new AdminException(AdminExceptionType.ADMIN_WRONG_PASSWORD); // 기존 비밀번호가 틀린 경우
        }

        //변경 비밀번호 형식 체크
        checkPasswordValid(newPassword);

        // 새로운 비밀번호로 변경 (암호화하여 저장)
        admin.setPassword(bCryptPasswordEncoder.encode(newPassword));

        // 변경된 비밀번호를 DB에 저장
        adminRepository.save(admin);

        // 성공 메시지 반환
        return new CheckResponse(true, "비밀번호가 성공적으로 변경되었습니다.");

    }

    //회원 삭제
    @Transactional
    public CheckResponse deleteAdmin(String email, String password) throws AdminException {

        // 이메일로 회원 찾기
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(()-> new AdminException(AdminExceptionType.ADMIN_NOT_EXIST));


        // 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(password, admin.getPassword())) {
            throw new AdminException(AdminExceptionType.ADMIN_WRONG_PASSWORD); // 비밀번호가 맞지 않으면 예외
        }

        // 회원 삭제
        adminRepository.delete(admin);

        // 성공적으로 삭제되었음을 알리는 메시지 반환
        return new CheckResponse(true, "관리자가 성공적으로 삭제되었습니다.");
    }


    private void isExistEmail(String email) {
        if(adminRepository.existsByEmail(email)){
            throw new AdminException(AdminExceptionType.ALREADY_EXIST_EMAIL);
        }
    }

    private void checkEmailValid(String email) {
        // 이메일 유효성 검사 정규표현식
        String EMAIL_FORMAT = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        if(email == null || !email.matches(EMAIL_FORMAT)){
            throw new AdminException(AdminExceptionType.INVALID_EMAIL_FORMAT);
        }
    }

    private void checkPasswordValid(String password) {
        // 사용자 비밀번호는 영문, 숫자, 하나 이상의 특수문자를 포함하는 8 ~ 16자
        String PASSWORD_FORMAT = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$";

        if(password == null ||!password.matches(PASSWORD_FORMAT)){
            throw new AdminException(AdminExceptionType.INVALID_PASSWORD_FORMAT);
        }
    }

    private void isExistName(String name) {
        if(adminRepository.existsByStoreName(name)){
            throw new AdminException(AdminExceptionType.ALREADY_EXIST_NAME);
        }
    }

}