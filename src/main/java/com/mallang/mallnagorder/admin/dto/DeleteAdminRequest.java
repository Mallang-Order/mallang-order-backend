package com.mallang.mallnagorder.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteAdminRequest {

    private String password; // 회원 삭제 시 비밀번호 확인

}
