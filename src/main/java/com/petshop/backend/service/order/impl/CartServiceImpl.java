package com.petshop.backend.service.order.impl;

import com.petshop.backend.dto.request.cart.AddToCartRequest;
import com.petshop.backend.dto.request.cart.MergeCartRequest;
import com.petshop.backend.dto.response.common.CartItemResponse;
import com.petshop.backend.dto.response.order.CartResponse;
import com.petshop.backend.entity.order.CartItem;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.entity.product.Store;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ConflictException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.order.CartItemRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.repository.product.StoreRepository;
import com.petshop.backend.security.CurrentUser;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.order.CartService;
import com.petshop.backend.service.order.ShippingCalculator;
import com.petshop.backend.service.product.ProductMapper;
import com.petshop.backend.util.MoneyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private static final int MAX_LINE_QUANTITY = 99;

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final ShippingCalculator shippingCalculator;

    public CartServiceImpl(CartItemRepository cartItemRepository, ProductRepository productRepository,
                           StoreRepository storeRepository, ShippingCalculator shippingCalculator) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.shippingCalculator = shippingCalculator;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(UserPrincipal currentUser) {
        return buildCart(CurrentUser.require(currentUser).getId());
    }

    @Override
    @Transactional
    public CartResponse addToCart(AddToCartRequest request, UserPrincipal currentUser) {
        Long userId = CurrentUser.require(currentUser).getId();
        Product product = purchasableProduct(request.getProductId());
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, product.getId())
                .orElseGet(() -> CartItem.builder().userId(userId).productId(product.getId()).quantity(0).build());
        int newQuantity = item.getQuantity() + request.getQuantity();
        checkStock(product, newQuantity);
        item.setQuantity(Math.min(newQuantity, MAX_LINE_QUANTITY));
        cartItemRepository.save(item);
        return buildCart(userId);
    }

    @Override
    @Transactional
    public CartResponse updateCartItemQuantity(Long itemId, Integer quantity, UserPrincipal currentUser) {
        Long userId = CurrentUser.require(currentUser).getId();
        CartItem item = cartItemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "id", itemId));
        if (quantity == null || quantity < 1) {
            throw new BadRequestException("INVALID_QUANTITY", "Quantity must be at least 1");
        }
        checkStock(purchasableProduct(item.getProductId()), quantity);
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return buildCart(userId);
    }

    @Override
    @Transactional
    public CartResponse removeFromCart(Long itemId, UserPrincipal currentUser) {
        Long userId = CurrentUser.require(currentUser).getId();
        CartItem item = cartItemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "id", itemId));
        cartItemRepository.delete(item);
        return buildCart(userId);
    }

    @Override
    @Transactional
    public CartResponse mergeCart(MergeCartRequest request, UserPrincipal currentUser) {
        Long userId = CurrentUser.require(currentUser).getId();
        Map<Long, Integer> wanted = new LinkedHashMap<>();
        for (AddToCartRequest line : request.getItems()) {
            wanted.merge(line.getProductId(), line.getQuantity(), Integer::sum);
        }
        Map<Long, Product> products = productRepository.findAllById(wanted.keySet()).stream()
                .filter(Product::isPubliclyVisible)
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        for (Map.Entry<Long, Integer> e : wanted.entrySet()) {
            Product product = products.get(e.getKey());
            if (product == null || stockOf(product) <= 0) continue;
            CartItem item = cartItemRepository.findByUserIdAndProductId(userId, product.getId())
                    .orElseGet(() -> CartItem.builder().userId(userId).productId(product.getId()).quantity(0).build());
            int quantity = Math.min(Math.min(item.getQuantity() + e.getValue(), stockOf(product)), MAX_LINE_QUANTITY);
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        return buildCart(userId);
    }

    @Override
    @Transactional
    public void clearCart(UserPrincipal currentUser) {
        cartItemRepository.deleteByUserId(CurrentUser.require(currentUser).getId());
    }

    private Product purchasableProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        if (!product.isPubliclyVisible()) {
            throw new BadRequestException("PRODUCT_UNAVAILABLE", "This product is no longer available");
        }
        return product;
    }

    private void checkStock(Product product, int quantity) {
        int stock = stockOf(product);
        if (stock <= 0) {
            throw new ConflictException("OUT_OF_STOCK", "'" + ProductMapper.localizedTitle(product) + "' is out of stock");
        }
        if (quantity > stock) {
            throw new ConflictException("INSUFFICIENT_STOCK", "Only " + stock + " of '" + ProductMapper.localizedTitle(product) + "' left");
        }
    }

    private static int stockOf(Product p) {
        return p.getStockQuantity() == null ? 0 : p.getStockQuantity();
    }

    private CartResponse buildCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserIdOrderByIdAsc(userId);
        Map<Long, Product> products = productRepository.findAllById(items.stream().map(CartItem::getProductId).toList())
                .stream().collect(Collectors.toMap(Product::getId, Function.identity()));
        Set<Long> storeIds = products.values().stream().map(Product::getStoreId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Store> stores = storeRepository.findAllById(storeIds).stream()
                .collect(Collectors.toMap(Store::getId, Function.identity()));

        List<CartItemResponse> lines = new ArrayList<>();
        Map<Long, BigDecimal> subtotalByStore = new HashMap<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal listTotal = BigDecimal.ZERO;
        int totalItems = 0;
        boolean ready = !items.isEmpty();

        for (CartItem item : items) {
            Product p = products.get(item.getProductId());
            if (p == null) continue;
            BigDecimal unit = MoneyUtils.effectivePrice(p.getPrice(), p.getDiscountPrice());
            BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(item.getQuantity()));
            int stock = stockOf(p);
            String problem = !p.isPubliclyVisible() ? "No longer available"
                    : stock <= 0 ? "Out of stock"
                    : item.getQuantity() > stock ? "Only " + stock + " left" : null;
            boolean available = problem == null;
            if (available) {
                subtotal = subtotal.add(lineTotal);
                listTotal = listTotal.add(MoneyUtils.nz(p.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())));
                totalItems += item.getQuantity();
                subtotalByStore.merge(p.getStoreId() == null ? -1L : p.getStoreId(), lineTotal, BigDecimal::add);
            } else {
                ready = false;
            }
            Store store = p.getStoreId() == null ? null : stores.get(p.getStoreId());
            lines.add(CartItemResponse.builder()
                    .id(item.getId())
                    .productId(p.getId())
                    .productTitle(ProductMapper.localizedTitle(p))
                    .productSlug(p.getSlug())
                    .productImage(ProductMapper.cover(p))
                    .storeId(p.getStoreId())
                    .storeName(store != null ? store.getName() : null)
                    .unitPrice(MoneyUtils.scale(unit))
                    .originalPrice(MoneyUtils.scale(p.getPrice()))
                    .quantity(item.getQuantity())
                    .subtotal(MoneyUtils.scale(lineTotal))
                    .stockQuantity(stock)
                    .available(available)
                    .availabilityMessage(problem)
                    .build());
        }

        BigDecimal shipping = subtotalByStore.values().stream()
                .map(shippingCalculator::feeFor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = subtotal.add(shipping);
        return CartResponse.builder()
                .items(lines)
                .totalItems(totalItems)
                .subtotal(MoneyUtils.scale(subtotal))
                .discount(MoneyUtils.scale(listTotal.subtract(subtotal)))
                .shippingFee(MoneyUtils.scale(shipping))
                .total(MoneyUtils.scale(total))
                .totalAmount(MoneyUtils.scale(total))
                .freeShippingThreshold(shippingCalculator.freeThreshold())
                .checkoutReady(ready)
                .build();
    }
}
