package com.petshop.backend.service.order.impl;

import com.petshop.backend.common.OrderStatus;
import com.petshop.backend.common.PaymentMethod;
import com.petshop.backend.common.PaymentStatus;
import com.petshop.backend.config.AppProperties;
import com.petshop.backend.dto.request.order.CancelOrderRequest;
import com.petshop.backend.dto.request.order.CreateOrderRequest;
import com.petshop.backend.dto.request.order.OrderItemRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.order.CheckoutResponse;
import com.petshop.backend.dto.response.order.OrderResponse;
import com.petshop.backend.entity.order.CartItem;
import com.petshop.backend.entity.order.Order;
import com.petshop.backend.entity.order.OrderItem;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.entity.product.Store;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.entity.user.UserAddress;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ConflictException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.order.CartItemRepository;
import com.petshop.backend.repository.order.OrderItemRepository;
import com.petshop.backend.repository.order.OrderRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.repository.product.StoreRepository;
import com.petshop.backend.repository.user.UserAddressRepository;
import com.petshop.backend.repository.user.UserRepository;
import com.petshop.backend.security.CurrentUser;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.coupon.CouponService;
import com.petshop.backend.service.order.*;
import com.petshop.backend.service.product.ProductMapper;
import com.petshop.backend.util.MoneyUtils;
import com.petshop.backend.util.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final String ORDER_NUMBER_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final UserAddressRepository addressRepository;
    private final CouponService couponService;
    private final ShippingCalculator shippingCalculator;
    private final OrderLifecycleService lifecycle;
    private final OrderMapper orderMapper;
    private final AppProperties.Shipping shipping;
    private final SecureRandom random = new SecureRandom();

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                            ProductRepository productRepository, StoreRepository storeRepository,
                            CartItemRepository cartItemRepository, UserRepository userRepository,
                            UserAddressRepository addressRepository, CouponService couponService,
                            ShippingCalculator shippingCalculator, OrderLifecycleService lifecycle,
                            OrderMapper orderMapper, AppProperties properties) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.couponService = couponService;
        this.shippingCalculator = shippingCalculator;
        this.lifecycle = lifecycle;
        this.orderMapper = orderMapper;
        this.shipping = properties.getShipping();
    }

    @Override
    @Transactional
    public CheckoutResponse createOrder(CreateOrderRequest request, UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        User buyer = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        boolean fromCart = request.getItems() == null || request.getItems().isEmpty();
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        if (fromCart) {
            List<CartItem> cart = cartItemRepository.findByUserIdOrderByIdAsc(buyer.getId());
            if (cart.isEmpty()) throw new BadRequestException("EMPTY_CART", "Your cart is empty");
            cart.forEach(c -> quantities.merge(c.getProductId(), c.getQuantity(), Integer::sum));
        } else {
            for (OrderItemRequest line : request.getItems()) {
                quantities.merge(line.getProductId(), line.getQuantity(), Integer::sum);
            }
        }

        String address = resolveAddress(request, buyer.getId());
        PaymentMethod paymentMethod = PaymentMethod.parse(request.getPaymentMethod());

        Map<Long, Product> products = productRepository.findAllByIdForUpdate(quantities.keySet()).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        List<String> problems = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : quantities.entrySet()) {
            Product p = products.get(e.getKey());
            if (p == null || !p.isPubliclyVisible()) {
                problems.add("Product " + e.getKey() + " is no longer available");
            } else {
                int stock = p.getStockQuantity() == null ? 0 : p.getStockQuantity();
                if (stock < e.getValue()) {
                    problems.add(stock <= 0 ? "'" + ProductMapper.localizedTitle(p) + "' is out of stock"
                            : "Only " + stock + " of '" + ProductMapper.localizedTitle(p) + "' left");
                }
            }
        }
        if (!problems.isEmpty()) {
            throw new ConflictException("STOCK_PROBLEM", String.join("; ", problems));
        }

        Map<Long, List<Product>> byStore = new LinkedHashMap<>();
        for (Long productId : quantities.keySet()) {
            Product p = products.get(productId);
            if (p.getStoreId() == null) {
                throw new ConflictException("PRODUCT_WITHOUT_STORE", "'" + ProductMapper.localizedTitle(p) + "' cannot be ordered");
            }
            byStore.computeIfAbsent(p.getStoreId(), k -> new ArrayList<>()).add(p);
        }
        Map<Long, Store> stores = storeRepository.findAllById(byStore.keySet()).stream()
                .collect(Collectors.toMap(Store::getId, Function.identity()));
        Map<Long, BigDecimal> storeSubtotals = new LinkedHashMap<>();
        byStore.forEach((storeId, list) -> storeSubtotals.put(storeId, list.stream()
                .map(p -> MoneyUtils.effectivePrice(p.getPrice(), p.getDiscountPrice())
                        .multiply(BigDecimal.valueOf(quantities.get(p.getId()))))
                .reduce(BigDecimal.ZERO, BigDecimal::add)));
        BigDecimal grandSubtotal = storeSubtotals.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        CouponService.Applied coupon = request.getCouponCode() == null || request.getCouponCode().isBlank()
                ? null : couponService.validate(request.getCouponCode(), grandSubtotal);
        Map<Long, BigDecimal> discounts = distribute(coupon == null ? BigDecimal.ZERO : coupon.discount(), storeSubtotals);

        String group = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        List<Order> created = new ArrayList<>();
        for (Map.Entry<Long, List<Product>> entry : byStore.entrySet()) {
            Long storeId = entry.getKey();
            Store store = stores.get(storeId);
            BigDecimal subtotal = MoneyUtils.scale(storeSubtotals.get(storeId));
            BigDecimal fee = shippingCalculator.feeFor(subtotal);
            BigDecimal discount = discounts.getOrDefault(storeId, BigDecimal.ZERO);

            Order order = orderRepository.save(Order.builder()
                    .buyerId(buyer.getId())
                    .orderNumber(newOrderNumber())
                    .checkoutGroup(group)
                    .storeId(storeId)
                    .vendorId(store != null ? store.getOwnerId() : entry.getValue().get(0).getVendorId())
                    .storeName(store != null ? store.getName() : null)
                    .subtotal(subtotal)
                    .shippingFee(fee)
                    .discountAmount(discount)
                    .couponCode(coupon == null ? null : coupon.coupon().getCode())
                    .totalAmount(MoneyUtils.scale(subtotal.add(fee).subtract(discount).max(BigDecimal.ZERO)))
                    .status(OrderStatus.PENDING)
                    .paymentStatus(PaymentStatus.PENDING)
                    .paymentMethod(paymentMethod)
                    .customerName(nonBlank(request.getCustomerName(), buyer.getFullName()))
                    .customerPhone(nonBlank(request.getCustomerPhone(), buyer.getPhone()))
                    .shippingAddress(address)
                    .note(request.getNote())
                    .estimatedDeliveryFrom(now.plusHours(shipping.getDeliveryHoursFrom()))
                    .estimatedDeliveryTo(now.plusHours(shipping.getDeliveryHoursTo()))
                    .archivedByVendor(false)
                    .build());

            List<OrderItem> lines = new ArrayList<>();
            for (Product p : entry.getValue()) {
                int qty = quantities.get(p.getId());
                BigDecimal unit = MoneyUtils.scale(MoneyUtils.effectivePrice(p.getPrice(), p.getDiscountPrice()));
                lines.add(OrderItem.builder()
                        .orderId(order.getId())
                        .productId(p.getId())
                        .storeId(storeId)
                        .productTitle(p.getTitle())
                        .productImage(ProductMapper.cover(p))
                        .originalPrice(MoneyUtils.scale(p.getPrice()))
                        .unitPrice(unit)
                        .quantity(qty)
                        .subtotal(MoneyUtils.scale(unit.multiply(BigDecimal.valueOf(qty))))
                        .build());
                p.setStockQuantity(p.getStockQuantity() - qty);
                p.setSoldCount((p.getSoldCount() == null ? 0 : p.getSoldCount()) + qty);
            }
            orderItemRepository.saveAll(lines);
            lifecycle.record(order.getId(), OrderStatus.PENDING, buyer.getId(), "Order placed");
            created.add(order);
        }
        productRepository.saveAll(products.values());

        if (coupon != null) couponService.incrementUsage(coupon.coupon().getId());
        if (fromCart) {
            cartItemRepository.deleteByUserIdAndProductIdIn(buyer.getId(), quantities.keySet());
        }

        List<OrderResponse> responses = orderMapper.toResponses(created);
        return CheckoutResponse.builder()
                .checkoutGroup(group)
                .orders(responses)
                .subtotal(MoneyUtils.scale(grandSubtotal))
                .shippingFee(MoneyUtils.scale(created.stream().map(Order::getShippingFee).reduce(BigDecimal.ZERO, BigDecimal::add)))
                .discountAmount(MoneyUtils.scale(created.stream().map(Order::getDiscountAmount).reduce(BigDecimal.ZERO, BigDecimal::add)))
                .grandTotal(MoneyUtils.scale(created.stream().map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add)))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getUserOrders(String status, String q, int page, int size, UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        Page<Order> result = orderRepository.findAll(
                OrderSpecifications.filter(currentUser.getId(), null, OrderStatus.parse(status), q, null, null, null),
                PageUtils.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt", "id")));
        return PageResponse.of(result, orderMapper.toResponses(result.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id, UserPrincipal currentUser) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        requireCanView(order, currentUser);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber, UserPrincipal currentUser) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
        requireCanView(order, currentUser);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long id, CancelOrderRequest request, UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        if (!order.getBuyerId().equals(currentUser.getId()) && !currentUser.isAdmin()) {
            throw new ResourceNotFoundException("Order", "id", id);
        }
        if (!order.getStatus().isCancellableByBuyer()) {
            throw new ConflictException("ORDER_NOT_CANCELLABLE",
                    "Orders can be cancelled only while pending or processing (current: " + order.getStatus().name().toLowerCase() + ")");
        }
        String reason = request == null || request.getReason() == null || request.getReason().isBlank()
                ? "Cancelled by buyer" : request.getReason().trim();
        return orderMapper.toResponse(lifecycle.transition(order, OrderStatus.CANCELLED, currentUser.getId(), reason));
    }

    private void requireCanView(Order order, UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        boolean allowed = currentUser.isAdmin()
                || order.getBuyerId().equals(currentUser.getId())
                || (order.getVendorId() != null && order.getVendorId().equals(currentUser.getId()));
        if (!allowed) throw new ResourceNotFoundException("Order", "id", order.getId());
    }

    private String resolveAddress(CreateOrderRequest request, Long userId) {
        if (request.getAddressId() != null) {
            UserAddress a = addressRepository.findByIdAndUserId(request.getAddressId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "id", request.getAddressId()));
            String text = a.getFullAddress() != null ? a.getFullAddress()
                    : String.join(", ", Arrays.stream(new String[]{a.getStreet(), a.getFloor(), a.getDistrict(), a.getCity()})
                        .filter(s -> s != null && !s.isBlank()).toList());
            return a.getNote() == null || a.getNote().isBlank() ? text : text + " (" + a.getNote() + ")";
        }
        if (request.getShippingAddress() != null && !request.getShippingAddress().isBlank()) {
            return request.getShippingAddress().trim();
        }
        return addressRepository.findFirstByUserIdAndIsDefaultTrue(userId)
                .map(UserAddress::getFullAddress)
                .orElseThrow(() -> new BadRequestException("ADDRESS_REQUIRED", "Send addressId or shippingAddress"));
    }

    private static Map<Long, BigDecimal> distribute(BigDecimal discount, Map<Long, BigDecimal> subtotals) {
        Map<Long, BigDecimal> result = new LinkedHashMap<>();
        if (discount.signum() <= 0) return result;
        BigDecimal total = subtotals.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal assigned = BigDecimal.ZERO;
        List<Long> ids = new ArrayList<>(subtotals.keySet());
        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            BigDecimal share = i == ids.size() - 1
                    ? discount.subtract(assigned)
                    : discount.multiply(subtotals.get(id)).divide(total, 2, RoundingMode.DOWN);
            result.put(id, MoneyUtils.scale(share));
            assigned = assigned.add(share);
        }
        return result;
    }

    private String newOrderNumber() {
        for (int attempt = 0; attempt < 10; attempt++) {
            StringBuilder sb = new StringBuilder("ORD-");
            for (int i = 0; i < 8; i++) sb.append(ORDER_NUMBER_ALPHABET.charAt(random.nextInt(ORDER_NUMBER_ALPHABET.length())));
            String number = sb.toString();
            if (!orderRepository.existsByOrderNumber(number)) return number;
        }
        throw new IllegalStateException("Could not generate a unique order number");
    }

    private static String nonBlank(String value, String fallback) {
        return value != null && !value.isBlank() ? value.trim() : fallback;
    }
}
