package com.petshop.backend.service.order;

import com.petshop.backend.common.OrderStatus;
import com.petshop.backend.entity.order.Order;
import com.petshop.backend.entity.order.OrderItem;
import com.petshop.backend.util.MaskUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class OrderSpecifications {

    private OrderSpecifications() {}

    public static Specification<Order> filter(Long buyerId, Long vendorId, OrderStatus status, String q,
                                              LocalDateTime from, LocalDateTime to, Boolean includeArchived) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (buyerId != null) p.add(cb.equal(root.get("buyerId"), buyerId));
            if (vendorId != null) p.add(cb.equal(root.get("vendorId"), vendorId));
            if (status != null) p.add(cb.equal(root.get("status"), status));
            if (from != null) p.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            if (to != null) p.add(cb.lessThan(root.get("createdAt"), to));
            if (Boolean.FALSE.equals(includeArchived)) {
                p.add(cb.or(cb.isNull(root.get("archivedByVendor")), cb.isFalse(root.get("archivedByVendor"))));
            }
            if (q != null && !q.isBlank()) {
                String pattern = "%" + MaskUtils.escapeLike(q.trim().toLowerCase(Locale.ROOT)) + "%";
                Subquery<Long> itemMatch = query.subquery(Long.class);
                var item = itemMatch.from(OrderItem.class);
                itemMatch.select(item.get("orderId")).where(
                        cb.equal(item.get("orderId"), root.get("id")),
                        cb.like(cb.lower(cb.coalesce(item.get("productTitle"), "")), pattern, '\\'));
                p.add(cb.or(
                        cb.like(cb.lower(root.get("orderNumber")), pattern, '\\'),
                        cb.like(cb.lower(cb.coalesce(root.get("storeName"), "")), pattern, '\\'),
                        cb.like(cb.lower(cb.coalesce(root.get("customerName"), "")), pattern, '\\'),
                        cb.exists(itemMatch)));
            }
            return cb.and(p.toArray(Predicate[]::new));
        };
    }
}
