package com.mallang.mallnagorder.category.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private Long adminId;
}
