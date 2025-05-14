package com.mallang.mallnagorder.kiosk.service;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.kiosk.domain.Kiosk;
import com.mallang.mallnagorder.kiosk.exception.KioskException;
import com.mallang.mallnagorder.kiosk.exception.KioskExceptionType;
import com.mallang.mallnagorder.kiosk.repository.KioskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class KioskService {

    private final KioskRepository kioskRepository;

    public void setKiosks(Admin admin, int count){

        // 1. 기존 Kiosk 불러오기
        List<Kiosk> existingKiosks = kioskRepository.findByAdmin(admin);

        // 2. 사용 중인 Kiosk가 있는지 확인
        boolean hasActiveKiosk = existingKiosks.stream()
                .anyMatch(Kiosk::getIsActive);

        if (hasActiveKiosk) {
            throw new KioskException(KioskExceptionType.ACTIVE_KIOSK_EXISTS);
        }

        // 3. 기존 키오스크 삭제
        kioskRepository.deleteAll(existingKiosks);

        // 4. 새 키오스크 생성
        List<Kiosk> newKiosks = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            newKiosks.add(Kiosk.builder()
                    .kioskNumber(i)
                    .isActive(false)
                    .admin(admin)
                    .build());
        }

        kioskRepository.saveAll(newKiosks);
    }

    public void activateKioskByNumber(Long adminId, int kioskNumber) {
        Kiosk kiosk = kioskRepository.findByAdminIdAndKioskNumber(adminId, kioskNumber)
                .orElseThrow(() -> new KioskException(KioskExceptionType.KIOSK_NOT_FOUND));

        if (Boolean.TRUE.equals(kiosk.getIsActive())) {
            throw new KioskException(KioskExceptionType.ACTIVE_KIOSK_EXISTS);
        }

        kiosk.setIsActive(true);
        kioskRepository.save(kiosk);
    }
}
