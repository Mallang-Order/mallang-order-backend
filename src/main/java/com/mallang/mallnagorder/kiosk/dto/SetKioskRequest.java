package com.mallang.mallnagorder.kiosk.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetKioskRequest {
    private Long adminId;
    private int count;
}
