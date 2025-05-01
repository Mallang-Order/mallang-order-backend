package com.mallang.mallnagorder.menu.domain;

import com.mallang.mallnagorder.admin.domain.Admin;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mendId;

    @Column(nullable = false, length = 100)
    private String menuName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal menuPrice;

    @Column(nullable = false, length = 2083)
    private String imageUrl = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;
}
