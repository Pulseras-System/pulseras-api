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
        // If status is 1 (cart), handle cart operations
        if (dto.getStatus() != null && dto.getStatus() == 1 && dto.getAccountId() != null) {
            return handleCartOperation(dto);
        }
        
        // Create new regular order (not a cart)
        Order order = OrderMapper.toEntity(dto);
        order.setCreateDate(LocalDateTime.now());
        return OrderMapper.toDTO(orderRepository.save(order));
    }
    
    // Helper method to handle cart operations
    private OrderDTO handleCartOperation(CreateOrderDTO dto) {
        String accountId = dto.getAccountId();
        
        // Check for existing cart
        List<Order> existingOrders = orderRepository.findByAccountId(accountId);
        Order existingCart = existingOrders.stream()
                .filter(order -> order.getStatus() != null && order.getStatus() == 1)
                .findFirst()
                .orElse(null);
        
        // If no products specified, just return existing cart or create empty cart
        if ((dto.getProductId() == null || dto.getProductId().trim().isEmpty()) && 
            (dto.getProductIds() == null || dto.getProductIds().isEmpty())) {
            
            if (existingCart != null) {
                // Update cart totals before returning
                updateCartTotals(existingCart);
                return OrderMapper.toDTO(existingCart);
            } else {
                // Create empty cart
                Order newCart = Order.builder()
                        .accountId(accountId)
                        .status(1)
                        .totalPrice(0.0)
                        .amount(0)
                        .orderInfor("Cart")
                        .createDate(LocalDateTime.now())
                        .lastEdited(LocalDateTime.now())
                        .build();
                return OrderMapper.toDTO(orderRepository.save(newCart));
            }
        }
        
        // Products are specified - add them to cart
        Order cartOrder = existingCart;
        
        if (cartOrder == null) {
            // Create new cart
            cartOrder = Order.builder()
                    .accountId(accountId)
                    .status(1)
                    .totalPrice(0.0)
                    .amount(0)
                    .orderInfor("Cart")
                    .createDate(LocalDateTime.now())
                    .lastEdited(LocalDateTime.now())
                    .build();
            cartOrder = orderRepository.save(cartOrder);
        }
        
        // Add products to cart
        if (dto.getProductId() != null && !dto.getProductId().trim().isEmpty()) {
            // Single product
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId()));
            addProductToCartWithIncrement(cartOrder, dto.getProductId(), product.getPrice().doubleValue());
        }
        
        if (dto.getProductIds() != null && !dto.getProductIds().isEmpty()) {
            // Multiple products
            for (String productId : dto.getProductIds()) {
                if (productId != null && !productId.trim().isEmpty()) {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
                    addProductToCartWithIncrement(cartOrder, productId, product.getPrice().doubleValue());
                }
            }
        }
        
        // Return updated cart
        return OrderMapper.toDTO(orderRepository.findById(cartOrder.getId()).orElse(cartOrder));
    }

    @Override
    public OrderDTO updateOrder(String id, CreateOrderDTO dto) {
        Order existing = orderRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        existing.setOrderInfor(dto.getOrderInfor());
        existing.setAmount(dto.getAmount());
        existing.setAccountId(dto.getAccountId());
        existing.setVoucherId(dto.getVoucherId());
        existing.setTotalPrice(dto.getTotalPrice());
        existing.setStatus(dto.getStatus());
        existing.setLastEdited(dto.getLastEdited());

        return OrderMapper.toDTO(orderRepository.save(existing));
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

        Map<String, Object> result = new HashMap<>();
        result.put("totalOrders", totalOrders);
        result.put("percentChange", percentChange);
        result.put("isIncrease", thisWeekOrders >= lastWeekOrders);
        result.put("thisWeekOrders", thisWeekOrders);
        result.put("lastWeekOrders", lastWeekOrders);

        return result;
    }

    @Override
    public Map<String, Object> totalGrowthWithCompare() {
        Map<String, Object> revenueData = totalRevenueWithCompare();
        Map<String, Object> orderData = totalOrdersWithCompare();
        Map<String, Object> customerData = accountService.totalCustomersWithCompare();

        double revenueChange = (double) revenueData.get("percentChange");
        double orderChange = (double) orderData.get("percentChange");
        double customerChange = (double) customerData.get("percentChange");

        // ✅ Trung bình phần trăm tăng trưởng
        double averageGrowth = (revenueChange + orderChange + customerChange) / 3;

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
    
    // Helper method to add product with +1 increment
    private void addProductToCartWithIncrement(Order cartOrder, String productId, Double price) {
        try {
            // Get product to check availability and update quantity
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
            
            // Check if product already exists in cart
            List<OrderDetail> existingDetails = orderDetailRepository.findByOrderId(cartOrder.getId().toString());
            OrderDetail existingDetail = existingDetails.stream()
                    .filter(detail -> productId.equals(detail.getProductId()))
                    .findFirst()
                    .orElse(null);
            
            if (existingDetail != null) {
                // Product exists - increment quantity by 1 and reactivate if needed
                if (existingDetail.getStatus() == 0) {
                    // Check product availability before reactivating
                    if (product.getQuantity() < 1) {
                        throw new IllegalStateException("Product is out of stock: " + product.getProductName());
                    }
                    
                    // Reactivate deleted item with quantity 1
                    existingDetail.setStatus(1);
                    existingDetail.setQuantity(1);
                    
                    // Decrease product quantity
                    product.setQuantity(product.getQuantity() - 1);
                    product.setLastEdited(LocalDateTime.now());
                    productRepository.save(product);
                    
                    System.out.println("Reactivated product " + productId + " in cart " + cartOrder.getId() + 
                                     ". Product stock decreased to: " + product.getQuantity());
                } else {
                    // Check product availability before incrementing
                    if (product.getQuantity() < 1) {
                        throw new IllegalStateException("Product is out of stock: " + product.getProductName());
                    }
                    
                    // Increment existing quantity by 1
                    int oldQuantity = existingDetail.getQuantity();
                    existingDetail.setQuantity(oldQuantity + 1);
                    
                    // Decrease product quantity
                    product.setQuantity(product.getQuantity() - 1);
                    product.setLastEdited(LocalDateTime.now());
                    productRepository.save(product);
                    
                    System.out.println("Updated product " + productId + " quantity from " + oldQuantity + " to " + (oldQuantity + 1) +
                                     ". Product stock decreased to: " + product.getQuantity());
                }
                existingDetail.setLastEdited(LocalDateTime.now());
                orderDetailRepository.save(existingDetail);
            } else {
                // Check product availability before creating new cart item
                if (product.getQuantity() < 1) {
                    throw new IllegalStateException("Product is out of stock: " + product.getProductName());
                }
                
                // Create new order detail with quantity 1
                OrderDetail newDetail = OrderDetail.builder()
                        .orderId(cartOrder.getId().toString())
                        .productId(productId)
                        .quantity(1) // Always start with 1
                        .price(price)
                        .promotionId(0)
                        .status(1)
                        .createDate(LocalDateTime.now())
                        .lastEdited(LocalDateTime.now())
                        .build();
                orderDetailRepository.save(newDetail);
                
                // Decrease product quantity
                product.setQuantity(product.getQuantity() - 1);
                product.setLastEdited(LocalDateTime.now());
                productRepository.save(product);
                
                System.out.println("Added new product " + productId + " to cart " + cartOrder.getId() + " with quantity 1" +
                                 ". Product stock decreased to: " + product.getQuantity());
            }
            
            // Update cart totals
            updateCartTotals(cartOrder);
            
        } catch (Exception e) {
            System.err.println("Failed to add product " + productId + " to cart " + cartOrder.getId() + ": " + e.getMessage());
            throw new RuntimeException("Failed to add product to cart: " + e.getMessage(), e);
        }
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
}
