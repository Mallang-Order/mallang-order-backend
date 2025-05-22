package com.mallang.mallnagorder.category.repository;

import com.mallang.mallnagorder.category.domain.Category;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.menus WHERE c.admin.id = :adminId")
    List<Category> findAllWithMenusByAdminId(@Param("adminId") Long adminId);

    @Query("SELECT DISTINCT c.admin.id FROM Category c")
    List<Long> findAllAdminIds();

}