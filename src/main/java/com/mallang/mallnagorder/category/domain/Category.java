package com.mallang.mallnagorder.category.domain;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.global.entity.BaseEntity;
import com.mallang.mallnagorder.menu.domain.Menu;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String categoryName;

    @Column(nullable = false, length = 100)
    private String categoryNameEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @ManyToMany(mappedBy = "categories")
    private List<Menu> menus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(getId(), category.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }


}
