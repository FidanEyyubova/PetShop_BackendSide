package com.petshop.backend.repository.order;

import com.petshop.backend.common.OrderStatus;
import com.petshop.backend.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Optional<Order> findByOrderNumber(String orderNumber);

    boolean existsByOrderNumber(String orderNumber);

    long countByBuyerIdAndStatusIn(Long buyerId, Collection<OrderStatus> statuses);

    long countByBuyerId(Long buyerId);

    long countByVendorIdAndStatusIn(Long vendorId, Collection<OrderStatus> statuses);

    List<Order> findByVendorIdAndCreatedAtBetween(Long vendorId, LocalDateTime from, LocalDateTime to);

    List<Order> findByVendorIdAndStatus(Long vendorId, OrderStatus status);

    List<Order> findByVendorIdAndStatusIn(Long vendorId, Collection<OrderStatus> statuses);

    List<Order> findByVendorIdAndStatusInAndArchivedByVendorFalse(Long vendorId, Collection<OrderStatus> statuses);

    @Query(value = "SELECT DISTINCT to_char(created_at, 'YYYY-MM') AS ym FROM orders " +
                   "WHERE vendor_id = :vendorId ORDER BY ym DESC", nativeQuery = true)
    List<String> findOrderMonths(@Param("vendorId") Long vendorId);

    @Query("SELECT COUNT(o) > 0 FROM Order o, OrderItem i WHERE i.orderId = o.id AND o.buyerId = :buyerId " +
           "AND i.productId = :productId AND o.status = com.petshop.backend.common.OrderStatus.DELIVERED")
    boolean hasDeliveredProduct(@Param("buyerId") Long buyerId, @Param("productId") Long productId);

    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE o.buyerId = :buyerId AND o.storeId = :storeId " +
           "AND o.status = com.petshop.backend.common.OrderStatus.DELIVERED")
    boolean hasDeliveredFromStore(@Param("buyerId") Long buyerId, @Param("storeId") Long storeId);
}
