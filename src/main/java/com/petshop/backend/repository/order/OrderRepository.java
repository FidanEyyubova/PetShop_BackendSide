package com.petshop.backend.repository.order;

import com.petshop.backend.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerId(Long buyerId);
    Page<Order> findByBuyerId(Long buyerId, Pageable pageable);
    List<Order> findByBuyerIdAndStatus(Long buyerId, String status);
    Optional<Order> findByOrderNumber(String orderNumber);
}