package com.petshop.backend.service.vendor;

import com.petshop.backend.common.Language;
import com.petshop.backend.common.OrderStatus;
import com.petshop.backend.config.AppProperties;
import com.petshop.backend.dto.response.vendor.SalesSeriesResponse;
import com.petshop.backend.dto.response.vendor.VendorDashboardResponse;
import com.petshop.backend.entity.order.Order;
import com.petshop.backend.entity.product.Store;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.repository.order.OrderItemRepository;
import com.petshop.backend.repository.order.OrderRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.repository.product.StoreRepository;
import com.petshop.backend.security.CurrentUser;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.util.MoneyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class VendorDashboardService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final int lowStockThreshold;

    public VendorDashboardService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                                  ProductRepository productRepository, StoreRepository storeRepository,
                                  AppProperties properties) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.lowStockThreshold = properties.getFinance().getLowStockThreshold();
    }

    @Transactional(readOnly = true)
    public VendorDashboardResponse summary(UserPrincipal currentUser) {
        Long vendorId = CurrentUser.require(currentUser).getId();
        YearMonth thisMonth = YearMonth.now();
        BigDecimal current = revenue(vendorId, thisMonth.atDay(1).atStartOfDay(), thisMonth.plusMonths(1).atDay(1).atStartOfDay());
        YearMonth lastMonth = thisMonth.minusMonths(1);
        BigDecimal previous = revenue(vendorId, lastMonth.atDay(1).atStartOfDay(), thisMonth.atDay(1).atStartOfDay());
        BigDecimal change = previous.signum() == 0 ? null
                : current.subtract(previous).multiply(BigDecimal.valueOf(100)).divide(previous, 1, RoundingMode.HALF_UP);

        Optional<Store> store = storeRepository.findFirstByOwnerIdOrderByIdAsc(vendorId);
        return VendorDashboardResponse.builder()
                .revenueThisMonth(MoneyUtils.scale(current))
                .revenueLastMonth(MoneyUtils.scale(previous))
                .revenueChangePercent(change)
                .activeOrders(orderRepository.countByVendorIdAndStatusIn(vendorId, OrderStatus.ACTIVE))
                .newOrders(orderRepository.countByVendorIdAndStatusIn(vendorId, EnumSet.of(OrderStatus.PENDING)))
                .soldProducts(orderItemRepository.sumQuantityByVendorAndStatuses(vendorId,
                        EnumSet.of(OrderStatus.PENDING, OrderStatus.PROCESSING, OrderStatus.SHIPPING, OrderStatus.DELIVERED)))
                .totalProducts(productRepository.countByVendorIdAndDeletedAtIsNull(vendorId))
                .lowStockProducts(productRepository.countLowStock(vendorId, lowStockThreshold))
                .rating(store.map(Store::getRating).orElse(BigDecimal.ZERO))
                .reviewCount(store.map(s -> s.getReviewCount() == null ? 0 : s.getReviewCount()).orElse(0))
                .accountStatus(currentUser.getStatus().name())
                .storeVerified(store.map(s -> Boolean.TRUE.equals(s.getVerified())).orElse(false))
                .build();
    }

    @Transactional(readOnly = true)
    public SalesSeriesResponse sales(String range, UserPrincipal currentUser) {
        Long vendorId = CurrentUser.require(currentUser).getId();
        String r = range == null ? "monthly" : range.trim().toLowerCase(Locale.ROOT);
        List<String> labels = new ArrayList<>();
        List<BigDecimal> revenue = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        if (r.equals("weekly")) {
            LocalDate today = LocalDate.now();
            LocalDate start = today.minusDays(6);
            List<Order> orders = orders(vendorId, start.atStartOfDay(), today.plusDays(1).atStartOfDay());
            for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {
                LocalDate day = d;
                List<Order> sameDay = orders.stream().filter(o -> o.getCreatedAt().toLocalDate().equals(day)).toList();
                labels.add(day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Language.current().locale()));
                revenue.add(MoneyUtils.scale(sumTotals(sameDay)));
                counts.add((long) sameDay.size());
            }
        } else if (r.equals("monthly")) {
            YearMonth now = YearMonth.now();
            LocalDateTime from = now.withMonth(1).atDay(1).atStartOfDay();
            List<Order> orders = orders(vendorId, from, now.plusMonths(1).atDay(1).atStartOfDay());
            for (int m = 1; m <= now.getMonthValue(); m++) {
                YearMonth ym = now.withMonth(m);
                List<Order> sameMonth = orders.stream().filter(o -> YearMonth.from(o.getCreatedAt()).equals(ym)).toList();
                labels.add(ym.getMonth().getDisplayName(TextStyle.SHORT, Language.current().locale()));
                revenue.add(MoneyUtils.scale(sumTotals(sameMonth)));
                counts.add((long) sameMonth.size());
            }
        } else {
            throw new BadRequestException("INVALID_RANGE", "range must be weekly or monthly");
        }
        return SalesSeriesResponse.builder().range(r).labels(labels).revenue(revenue).orders(counts).build();
    }

    private BigDecimal revenue(Long vendorId, LocalDateTime from, LocalDateTime to) {
        return sumTotals(orders(vendorId, from, to));
    }

    private List<Order> orders(Long vendorId, LocalDateTime from, LocalDateTime to) {
        return orderRepository.findByVendorIdAndCreatedAtBetween(vendorId, from, to.minusNanos(1)).stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .toList();
    }

    private static BigDecimal sumTotals(List<Order> orders) {
        return orders.stream().map(o -> MoneyUtils.nz(o.getTotalAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
