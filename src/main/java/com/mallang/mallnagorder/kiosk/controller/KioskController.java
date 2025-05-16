package com.mallang.mallnagorder.kiosk.controller;

import com.mallang.mallnagorder.admin.dto.AdminDetails;
import com.mallang.mallnagorder.kiosk.service.KioskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kiosk")
@RequiredArgsConstructor
public class KioskController {

    private final KioskService kioskService;

    @PostMapping("/set")
    public ResponseEntity<String> setKiosks(@AuthenticationPrincipal AdminDetails adminDetails,
                                            @RequestParam int count) {

        kioskService.setKiosks(adminDetails.getAdmin(), count);
        return ResponseEntity.ok("테이블 정보가 성공적으로 설정되었습니다.");
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activateKiosk(
            @AuthenticationPrincipal AdminDetails adminDetails,
            @RequestParam int kioskNumber) {

        kioskService.activateKioskByNumber(adminDetails.getAdmin().getId(), kioskNumber);
        return ResponseEntity.ok(kioskNumber + "번 테이블이 성공적으로 활성화되었습니다.");
    }

}
