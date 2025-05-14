package com.mallang.mallnagorder.kiosk.controller;


import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.admin.repository.AdminRepository;
import com.mallang.mallnagorder.kiosk.dto.SetKioskRequest;
import com.mallang.mallnagorder.kiosk.service.KioskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kiosks")
@RequiredArgsConstructor
public class KioskController {

    private final KioskService kioskService;
    private final AdminRepository adminRepository;

    @PostMapping("/set")
    public ResponseEntity<String> setKiosks(@RequestBody SetKioskRequest request) {
        Admin admin = adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new IllegalArgumentException("해당 관리자를 찾을 수 없습니다."));

        kioskService.setKiosks(admin, request.getCount());

        return ResponseEntity.ok("테이블 정보가 성공적으로 설정되었습니다.");
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activateKiosk(
            @RequestParam Long adminId,
            @RequestParam int kioskNumber) {
        kioskService.activateKioskByNumber(adminId, kioskNumber);
        return ResponseEntity.ok(kioskNumber + "번 테이블이 성공적으로 활성화되었습니다.");
    }

}
