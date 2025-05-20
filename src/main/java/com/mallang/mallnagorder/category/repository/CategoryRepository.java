package com.mallang.mallnagorder.category.repository;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByCategoryNameAndAdminId(String categoryName, Long adminId);
    boolean existsByCategoryNameEnAndAdminId(String categoryNameEn, Long adminId);

    Optional<Category> findByCategoryNameAndAdminId(String categoryName, Long adminId);

    Optional<Category> findByIdAndAdminId(Long categoryId, Long adminId);

    boolean existsByCategoryNameAndAdminIdAndIdNot(String name, Long adminId, Long id);
    boolean existsByCategoryNameEnAndAdminIdAndIdNot(String nameEn, Long adminId, Long id);

}