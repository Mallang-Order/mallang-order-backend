package com.mallang.mallnagorder.menu.domain;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.category.domain.Category;
import com.mallang.mallnagorder.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String menuName;

    @Column(nullable = false, length = 100)
    private String menuNameEn;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal menuPrice;

    @Column(nullable = false, length = 2083)
    private String imageUrl = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @ManyToMany
    @JoinTable(
            name = "menu_category",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

}
