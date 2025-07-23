package com.pulseras.api.service.impl;

import com.pulseras.api.dto.*;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.OrderMapper;
import com.pulseras.api.entity.Order;
import com.pulseras.api.entity.OrderDetail;
import com.pulseras.api.entity.Product;
import com.pulseras.api.repository.OrderRepository;
import com.pulseras.api.repository.OrderDetailRepository;
import com.pulseras.api.repository.ProductRepository;
import com.pulseras.api.service.AccountService;
import com.pulseras.api.service.OrderService;
import com.pulseras.api.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    private final AccountService accountService;
    private final VoucherService voucherService;

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(String id) {
        Order order = orderRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return OrderMapper.toDTO(order);
    }

    @Override
    public OrderDTO createOrder(CreateOrderDTO dto) {
        // Validate voucher ownership if voucher is provided
        if (dto.getVoucherId() != null && dto.getAccountId() != null) {
            validateVoucherOwnership(dto.getVoucherId(), dto.getAccountId());
        }
        
        // Create new regular order (not a cart)
        Order order = OrderMapper.toEntity(dto);
        order.setCreateDate(LocalDateTime.now());
        Order saved = orderRepository.save(order);
        
        // Mark voucher as used if order is completed
        markVoucherAsUsedIfOrderCompleted(saved);
        
        return OrderMapper.toDTO(saved);
    }
    @Override
    public OrderDTO updateOrder(String id, CreateOrderDTO dto) {
        Order existing = orderRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Validate voucher ownership if voucher is provided
        if (dto.getVoucherId() != null && dto.getAccountId() != null) {
            validateVoucherOwnership(dto.getVoucherId(), dto.getAccountId());
        }

        existing.setOrderInfor(dto.getOrderInfor());
        existing.setAmount(dto.getAmount());
        existing.setAccountId(dto.getAccountId());
        existing.setVoucherId(dto.getVoucherId());
        existing.setTotalPrice(dto.getTotalPrice());
        existing.setStatus(dto.getStatus());
        existing.setLastEdited(dto.getLastEdited());

        Order saved = orderRepository.save(existing);
        
        // Mark voucher as used if order is completed
        markVoucherAsUsedIfOrderCompleted(saved);

        return OrderMapper.toDTO(saved);
    }

    @Override
    public void deleteOrder(String id) {
        ObjectId objId = new ObjectId(id);
        if (!orderRepository.existsById(objId)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(objId);
    }

    @Override
    public List<OrderDTO> getOrdersByAccountId(String accountId){
        return orderRepository.findByAccountId(accountId).stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO partialUpdateOrder(String id, UpdateOrderDTO dto) {
        Order existing = orderRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Validate voucher ownership if voucher is being updated
        if (dto.getVoucherId() != null && existing.getAccountId() != null) {
            validateVoucherOwnership(dto.getVoucherId(), existing.getAccountId());
        }

        // Check if status is changing from cart (1) to completed (not 1)
        boolean isCartBeingCompleted = existing.getStatus() != null && existing.getStatus() == 1 &&
                                     dto.getStatus() != null && dto.getStatus() != 1;

        if (dto.getOrderInfor() != null) existing.setOrderInfor(dto.getOrderInfor());
        if (dto.getAmount() != null) existing.setAmount(dto.getAmount());
        if (dto.getVoucherId() != null) existing.setVoucherId(dto.getVoucherId());
        if (dto.getTotalPrice() != null) existing.setTotalPrice(dto.getTotalPrice());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());

        existing.setLastEdited(java.time.LocalDateTime.now());

        Order saved = orderRepository.save(existing);
        
        // If cart was completed, we don't need to restore quantities as products are being purchased
        // The quantities were already reserved when added to cart
        if (isCartBeingCompleted) {
            System.out.println("Cart " + id + " completed. Product quantities remain reserved (purchased).");
        }
        
        // Mark voucher as used if order is completed
        markVoucherAsUsedIfOrderCompleted(saved);

        return OrderMapper.toDTO(saved);
    }
    
    /**
     * Method to restore product quantities for cancelled cart orders
     */
    public void restoreCartProductQuantities(String cartOrderId) {
        try {
            Order cartOrder = orderRepository.findById(new ObjectId(cartOrderId)).orElse(null);
            if (cartOrder == null || cartOrder.getStatus() == null || cartOrder.getStatus() != 1) {
                return; // Not a cart order
            }
            
            // Get all active cart items
            List<OrderDetail> cartItems = orderDetailRepository.findByOrderId(cartOrderId)
                    .stream()
                    .filter(detail -> detail.getStatus() != null && detail.getStatus() == 1)
                    .toList();
            
            // Restore quantities for each product
            for (OrderDetail cartItem : cartItems) {
                Product product = productRepository.findById(cartItem.getProductId()).orElse(null);
                if (product != null) {
                    product.setQuantity(product.getQuantity() + cartItem.getQuantity());
                    product.setLastEdited(LocalDateTime.now());
                    productRepository.save(product);
                    System.out.println("Restored " + cartItem.getQuantity() + " units for product " + 
                                     cartItem.getProductId() + ". New stock: " + product.getQuantity());
                }
                
                // Deactivate cart item
                cartItem.setStatus(0);
                cartItem.setLastEdited(LocalDateTime.now());
                orderDetailRepository.save(cartItem);
            }
            
            // Update cart totals
            cartOrder.setAmount(0);
            cartOrder.setTotalPrice(0.0);
            cartOrder.setStatus(0); // Deactivate cart
            cartOrder.setLastEdited(LocalDateTime.now());
            orderRepository.save(cartOrder);
            
        } catch (Exception e) {
            System.err.println("Failed to restore cart product quantities for order " + cartOrderId + ": " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> totalRevenueWithCompare() {
        LocalDate today = LocalDate.now();
        LocalDate startOfThisWeek = today.with(DayOfWeek.MONDAY);
        LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
        LocalDate endOfLastWeek = startOfThisWeek.minusDays(1);

        // Convert to LocalDateTime
        LocalDateTime startThisWeek = startOfThisWeek.atStartOfDay();
        LocalDateTime startLastWeek = startOfLastWeek.atStartOfDay();
        LocalDateTime endLastWeek = endOfLastWeek.atTime(LocalTime.MAX);

        // ✅ Tổng doanh thu toàn bộ lịch sử (status ≠ 0 && status ≠ 1)
        BigDecimal totalRevenue = orderRepository.findAll()
                .stream()
                .filter(order -> order.getStatus() != null && order.getStatus() != 0 && order.getStatus() != 1)
                .map(order -> BigDecimal.valueOf(order.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        // ✅ Doanh thu tuần này
        BigDecimal thisWeekRevenue = orderRepository.findByCreateDateBetween(startThisWeek, LocalDateTime.now())
                .stream()
                .filter(order -> order.getStatus() != null && order.getStatus() != 0 && order.getStatus() != 1)
                .map(order -> BigDecimal.valueOf(order.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ;

        // ✅ Doanh thu tuần trước
        BigDecimal lastWeekRevenue = orderRepository.findByCreateDateBetween(startLastWeek, endLastWeek)
                .stream()
                .filter(order -> order.getStatus() != null && order.getStatus() != 0 && order.getStatus() != 1)
                .map(order -> BigDecimal.valueOf(order.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ;

        // ✅ Tính phần trăm thay đổi
        double percentChange = 0;
        if (lastWeekRevenue.compareTo(BigDecimal.ZERO) > 0) {
            percentChange = thisWeekRevenue.subtract(lastWeekRevenue)
                    .divide(lastWeekRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        } else if (thisWeekRevenue.compareTo(BigDecimal.ZERO) > 0) {
            percentChange = 100;
        }

        percentChange = formatToTwoDecimalPlaces(percentChange);
        Map<String, Object> result = new HashMap<>();
        result.put("totalRevenue", totalRevenue);
        result.put("percentChange", percentChange);
        result.put("isIncrease", thisWeekRevenue.compareTo(lastWeekRevenue) >= 0);
        result.put("thisWeekRevenue", thisWeekRevenue);
        result.put("lastWeekRevenue", lastWeekRevenue);

        return result;
    }


    @Override
    public Map<String, Object> totalOrdersWithCompare() {
        LocalDate today = LocalDate.now();
        LocalDate startOfThisWeek = today.with(DayOfWeek.MONDAY);
        LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
        LocalDate endOfLastWeek = startOfThisWeek.minusDays(1);

        // Convert to LocalDateTime
        LocalDateTime startThisWeek = startOfThisWeek.atStartOfDay();
        LocalDateTime startLastWeek = startOfLastWeek.atStartOfDay();
        LocalDateTime endLastWeek = endOfLastWeek.atTime(LocalTime.MAX);

        // ✅ Tổng toàn bộ đơn hàng trong lịch sử (status ≠ 0 && status ≠ 1)
        long totalOrders = orderRepository.findAll()
                .stream()
                .filter(order -> order.getStatus() != null && order.getStatus() != 0 && order.getStatus() != 1)
                .count();

        // ✅ Tổng đơn hàng tuần này
        long thisWeekOrders = orderRepository.findByCreateDateBetween(startThisWeek, LocalDateTime.now())
                .stream()
                .filter(order -> order.getStatus() != null && order.getStatus() != 0 && order.getStatus() != 1)
                .count();

        // ✅ Tổng đơn hàng tuần trước
        long lastWeekOrders = orderRepository.findByCreateDateBetween(startLastWeek, endLastWeek)
                .stream()
                .filter(order -> order.getStatus() != null && order.getStatus() != 0 && order.getStatus() != 1)
                .count();

        // ✅ Tính phần trăm thay đổi
        double percentChange = 0;
        if (lastWeekOrders > 0) {
            percentChange = ((double)(thisWeekOrders - lastWeekOrders) / lastWeekOrders) * 100;
        } else if (thisWeekOrders > 0) {
            percentChange = 100;
        }

        percentChange = formatToTwoDecimalPlaces(percentChange);

        Map<String, Object> result = new HashMap<>();
        result.put("totalOrders", totalOrders);
        result.put("percentChange", percentChange);
        result.put("isIncrease", thisWeekOrders >= lastWeekOrders);
        result.put("thisWeekOrders", thisWeekOrders);
        result.put("lastWeekOrders", lastWeekOrders);

        return result;
    }

    private double formatToTwoDecimalPlaces(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Override
    public Map<String, Object> totalGrowthWithCompare() {
        Map<String, Object> revenueData = totalRevenueWithCompare();
        Map<String, Object> orderData = totalOrdersWithCompare();
        Map<String, Object> customerData = accountService.totalCustomersWithCompare();

        double revenueChange = formatToTwoDecimalPlaces((double) revenueData.get("percentChange"));
        double orderChange = formatToTwoDecimalPlaces((double) orderData.get("percentChange"));
        double customerChange = formatToTwoDecimalPlaces((double) customerData.get("percentChange"));

        // ✅ Trung bình phần trăm tăng trưởng
        double averageGrowth = formatToTwoDecimalPlaces((revenueChange + orderChange + customerChange) / 3);

        // ✅ Xác định có tăng hay không
        boolean isIncrease = averageGrowth >= 0;

        Map<String, Object> result = new HashMap<>();
        result.put("growthRate", averageGrowth);
        result.put("isIncrease", isIncrease);
        result.put("revenueChange", revenueChange);
        result.put("orderChange", orderChange);
        result.put("customerChange", customerChange);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public AggregatedOverview getOverview() {
        LocalDate today = LocalDate.now();

        return new AggregatedOverview(
                buildWeekly(today),
                buildMonthly(today),
                buildYearly(today.getYear())
        );
    }

    private List<DailyOverview> buildWeekly(LocalDate today) {
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        return IntStream.range(0, 7)
                .mapToObj(i -> {
                    LocalDate current = monday.plusDays(i);
                    return aggregatePerDay(current,
                            i == 6 ? "CN" : "T" + (i + 2));
                })
                .toList();
    }

    private List<DailyOverview> buildMonthly(LocalDate today) {
        YearMonth ym = YearMonth.from(today);

        return IntStream.rangeClosed(1, ym.lengthOfMonth())
                .mapToObj(day -> aggregatePerDay(ym.atDay(day),
                        String.valueOf(day)))
                .toList();
    }

    private List<MonthlyOverview> buildYearly(int year) {
        return IntStream.rangeClosed(1, 12)
                .mapToObj(m -> aggregatePerMonth(YearMonth.of(year, m),
                        "T" + m))
                .toList();
    }

    private DailyOverview aggregatePerDay(LocalDate date, String label) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.atTime(LocalTime.MAX);

        List<Order> orders = orderRepository
                .findByCreateDateBetween(start, end)
                .stream()
                .filter(o -> isFinished(o.getStatus()))
                .toList();

        long count = orders.size();
        BigDecimal revenue = orders.stream()
                .map(o -> BigDecimal.valueOf(o.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DailyOverview(label, count, revenue);
    }

    private MonthlyOverview aggregatePerMonth(YearMonth ym, String label) {
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end   = ym.atEndOfMonth().atTime(LocalTime.MAX);

        List<Order> orders = orderRepository
                .findByCreateDateBetween(start, end)
                .stream()
                .filter(o -> isFinished(o.getStatus()))
                .toList();

        long count = orders.size();
        BigDecimal revenue = orders.stream()
                .map(o -> BigDecimal.valueOf(o.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MonthlyOverview(label, count, revenue);
    }

    private boolean isFinished(Integer status) {
        return status != null && status != 0 && status != 1;
    }
    
    // Helper method to update cart totals with new product
    private void updateCartTotals(Order cartOrder) {
        try {
            // Get all active order details for this cart
            List<OrderDetail> activeDetails = orderDetailRepository.findByOrderId(cartOrder.getId().toString())
                    .stream()
                    .filter(detail -> detail.getStatus() != null && detail.getStatus() == 1)
                    .toList();
            
            if (activeDetails.isEmpty()) {
                // No active items in cart
                cartOrder.setAmount(0);
                cartOrder.setTotalPrice(0.0);
                System.out.println("Cart " + cartOrder.getId() + " has no active items, set totals to 0");
            } else {
                // Calculate totals
                int totalAmount = activeDetails.stream()
                        .mapToInt(detail -> detail.getQuantity() != null ? detail.getQuantity() : 0)
                        .sum();
                
                double totalPrice = activeDetails.stream()
                        .mapToDouble(detail -> {
                            Double price = detail.getPrice();
                            Integer quantity = detail.getQuantity();
                            return (price != null ? price : 0.0) * (quantity != null ? quantity : 0);
                        })
                        .sum();
                
                // Update cart order
                cartOrder.setAmount(totalAmount);
                cartOrder.setTotalPrice(totalPrice);
                System.out.println("Updated cart " + cartOrder.getId() + " totals: amount=" + totalAmount + ", price=" + totalPrice);
            }
            
            cartOrder.setLastEdited(LocalDateTime.now());
            orderRepository.save(cartOrder);
            
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Failed to update cart totals for cartId: " + cartOrder.getId() + ", error: " + e.getMessage());
            // Set default values to prevent null issues
            cartOrder.setAmount(0);
            cartOrder.setTotalPrice(0.0);
            cartOrder.setLastEdited(LocalDateTime.now());
            orderRepository.save(cartOrder);
        }
    }
    
    @Override
    public OrderDTO addToCart(String accountId, String productId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("Account ID is required");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID is required");
        }
        
        try {
            // Get or create cart
            Order cartOrder = getOrCreateCart(accountId);
            
            // Get product to check availability
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
            
            // Check stock availability (always add just 1)
            if (product.getQuantity() < 1) {
                throw new IllegalStateException("Product is out of stock: " + product.getProductName());
            }
            
            // Always add product to cart with quantity 1
            addProductToCartWithQuantity(cartOrder, productId, product.getPrice().doubleValue());
            
            // Return updated cart
            return OrderMapper.toDTO(orderRepository.findById(cartOrder.getId()).orElse(cartOrder));
            
        } catch (Exception e) {
            System.err.println("Failed to add product to cart: " + e.getMessage());
            throw new RuntimeException("Failed to add product to cart: " + e.getMessage(), e);
        }
    }
    
    @Override
    public OrderDTO addMultipleToCart(String accountId, List<String> productIds) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("Account ID is required");
        }
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Product IDs list cannot be empty");
        }
        
        try {
            // Get or create cart
            Order cartOrder = getOrCreateCart(accountId);
            
            // Add each product to cart
            for (String productId : productIds) {
                if (productId != null && !productId.trim().isEmpty()) {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
                    
                    // Check stock availability
                    if (product.getQuantity() < 1) {
                        System.out.println("Skipping product " + productId + " - out of stock");
                        continue;
                    }
                    
                    // Add product with quantity 1
                    addProductToCartWithQuantity(cartOrder, productId, product.getPrice().doubleValue());
                }
            }
            
            return OrderMapper.toDTO(orderRepository.findById(cartOrder.getId()).orElse(cartOrder));
            
        } catch (Exception e) {
            System.err.println("Failed to add multiple products to cart: " + e.getMessage());
            throw new RuntimeException("Failed to add multiple products to cart: " + e.getMessage(), e);
        }
    }
    
    private Order getOrCreateCart(String accountId) {
        List<Order> existingOrders = orderRepository.findByAccountId(accountId);
        Order existingCart = existingOrders.stream()
                .filter(order -> order.getStatus() != null && order.getStatus() == 1)
                .findFirst()
                .orElse(null);
        
        if (existingCart != null) {
            return existingCart;
        }
        
        // Create new cart
        Order newCart = Order.builder()
                .accountId(accountId)
                .status(1)
                .totalPrice(0.0)
                .amount(0)
                .orderInfor("Cart")
                .createDate(LocalDateTime.now())
                .lastEdited(LocalDateTime.now())
                .build();
        orderRepository.save(newCart);
        return newCart;
    }
    

    private void addProductToCartWithQuantity(Order cartOrder, String productId, Double price) {
        try {
            // Get product to update quantity
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
            
            List<OrderDetail> existingDetails = orderDetailRepository.findByOrderId(cartOrder.getId().toString());
            OrderDetail existingDetail = existingDetails.stream()
                    .filter(detail -> productId.equals(detail.getProductId()))
                    .findFirst()
                    .orElse(null);
            
            if (existingDetail != null) {
                if (existingDetail.getStatus() == 0) {
                    existingDetail.setStatus(1);
                    existingDetail.setQuantity(1);
                    
                } else {
                    int oldQuantity = existingDetail.getQuantity();
                    existingDetail.setQuantity(oldQuantity + 1);
                    
                }
                existingDetail.setLastEdited(LocalDateTime.now());
                orderDetailRepository.save(existingDetail);
            } else {
                OrderDetail newDetail = OrderDetail.builder()
                        .orderId(cartOrder.getId().toString())
                        .productId(productId)
                        .quantity(1)
                        .price(price)
                        .promotionId(0)
                        .status(1)
                        .createDate(LocalDateTime.now())
                        .lastEdited(LocalDateTime.now())
                        .build();
                orderDetailRepository.save(newDetail);
                
            }
            
            product.setQuantity(product.getQuantity() - 1);
            product.setLastEdited(LocalDateTime.now());
            productRepository.save(product);
            
            System.out.println("Product " + productId + " stock decreased by 1. New stock: " + product.getQuantity());
            
            updateCartTotals(cartOrder);
            
        } catch (Exception e) {
            System.err.println("Failed to add product " + productId + " to cart " + cartOrder.getId() + ": " + e.getMessage());
            throw new RuntimeException("Failed to add product to cart: " + e.getMessage(), e);
        }
    }

    private void validateVoucherOwnership(String voucherId, String accountId) {
        if (voucherId != null && !voucherId.trim().isEmpty() && !voucherId.equals("0")) {
            if (!voucherService.isVoucherUsable(voucherId, accountId)) {
                throw new IllegalArgumentException("Voucher with id " + voucherId + " is not usable by account " + accountId + ". It may not be available to this account, already be used by this account, be expired, or be inactive.");
            }
        }
    }

    private void markVoucherAsUsedIfOrderCompleted(Order order) {
        if (order.getVoucherId() != null && !order.getVoucherId().trim().isEmpty() && !order.getVoucherId().equals("0")) {
            if (order.getStatus() != null && order.getStatus() != 0 && order.getStatus() != 1) {
                try {
                    voucherService.markVoucherAsUsed(order.getVoucherId(), order.getAccountId());
                    System.out.println("Marked voucher " + order.getVoucherId() + " as used by account " + order.getAccountId());
                } catch (Exception e) {
                    System.err.println("Failed to mark voucher as used: " + e.getMessage());
                }
            }
        }
    }
}
