package com.mallang.mallnagorder.kiosk.domain;

import com.mallang.mallnagorder.admin.domain.Admin;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kiosk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kioskId;

    @Column(nullable = false, length = 100)
    private String kioskName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;


}
