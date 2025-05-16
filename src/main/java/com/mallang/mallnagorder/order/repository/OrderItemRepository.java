package com.mallang.mallnagorder.order.repository;

import com.mallang.mallnagorder.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface
OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
