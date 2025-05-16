package com.mallang.mallnagorder.menu.repository;

import com.mallang.mallnagorder.menu.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    // 카테고리 ID로 해당 카테고리에 속한 메뉴 조회
    List<Menu> findByCategories_Id(Long categoryId);
}
