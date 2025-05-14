package com.mallang.mallnagorder.category.domain;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.global.entity.BaseEntity;
import com.mallang.mallnagorder.menu.domain.Menu;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @ManyToMany(mappedBy = "categories")
    private List<Menu> menus;

}