package com.mallang.mallnagorder.order.domain;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.global.entity.BaseEntity;
import com.mallang.mallnagorder.kiosk.domain.Kiosk;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private boolean isCompleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kiosk_id")
    private Kiosk kiosk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<OrderItem> orderItems = new java.util.ArrayList<>();
}
