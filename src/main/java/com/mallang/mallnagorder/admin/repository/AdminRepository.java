package com.mallang.mallnagorder.admin.repository;

import com.mallang.mallnagorder.admin.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
}
