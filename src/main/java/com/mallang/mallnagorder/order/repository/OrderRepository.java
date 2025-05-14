package com.mallang.mallnagorder.order.repository;

import com.mallang.mallnagorder.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
