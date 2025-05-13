package com.mallang.mallnagorder.admin.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JoinRequest {

    private String email;
    private String password;
    private String adminName;
    private String storeName;

}