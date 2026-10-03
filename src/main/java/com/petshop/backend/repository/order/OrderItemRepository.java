package com.petshop.backend.repository.order;

import com.petshop.backend.entity.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByOrderIdIn(Collection<Long> orderIds);

    /** Units sold by a vendor in orders with the given statuses. */
    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM OrderItem i, Order o WHERE i.orderId = o.id " +
           "AND o.vendorId = :vendorId AND o.status IN :statuses")
    long sumQuantityByVendorAndStatuses(@Param("vendorId") Long vendorId,
                                        @Param("statuses") Collection<com.petshop.backend.common.OrderStatus> statuses);
}
