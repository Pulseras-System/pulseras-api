package com.pulseras.api.controller;

import com.pulseras.api.dto.AggregatedOverview;
import com.pulseras.api.dto.CreateOrderDTO;
import com.pulseras.api.dto.OrderDTO;
import com.pulseras.api.dto.UpdateOrderDTO;
import com.pulseras.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderDTO> getAll() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<OrderDTO> create(@RequestBody CreateOrderDTO dto) {
        return ResponseEntity.ok(orderService.createOrder(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> update(@PathVariable String id, @RequestBody CreateOrderDTO dto) {
        return ResponseEntity.ok(orderService.updateOrder(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/account/{id}")
    public List<OrderDTO> getOrdersByAccountId(@PathVariable String id) {
        return orderService.getOrdersByAccountId(id);
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> totalRevenue() {
        return ResponseEntity.ok(orderService.totalRevenueWithCompare());
    }

    @GetMapping("/total-orders")
    public ResponseEntity<Map<String, Object>> totalOrders() {
        return ResponseEntity.ok(orderService.totalOrdersWithCompare());
    }

    @GetMapping("/growth")
    public ResponseEntity<Map<String, Object>> totalGrowth() {
        return ResponseEntity.ok(orderService.totalGrowthWithCompare());
    }

    @GetMapping("/overview")      // FE chỉ call URL này
    public ResponseEntity<AggregatedOverview> getOverview() {
        return ResponseEntity.ok(orderService.getOverview());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderDTO> partialUpdate(@PathVariable String id, @RequestBody UpdateOrderDTO dto) {
        return ResponseEntity.ok(orderService.partialUpdateOrder(id, dto));
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<OrderDTO> addToCart(@RequestBody Map<String, Object> request) {
        try {
            String accountId = (String) request.get("accountId");
            Object productIdObj = request.get("productId");
            Object quantityObj = request.get("quantity");
            
            if (accountId == null || accountId.trim().isEmpty()) {
                throw new IllegalArgumentException("AccountId is required");
            }
            
            if (productIdObj == null) {
                throw new IllegalArgumentException("ProductId is required");
            }
            
            OrderDTO result;
            
            if (productIdObj instanceof List) {
                // Handle multiple productIds
                @SuppressWarnings("unchecked")
                List<String> productIds = (List<String>) productIdObj;
                if (productIds.isEmpty()) {
                    throw new IllegalArgumentException("ProductId list cannot be empty");
                }
                result = orderService.addMultipleToCart(accountId, productIds);
            } else {
                // Handle single productId
                String productId = (String) productIdObj;
                Integer quantity = null;
                
                if (quantityObj != null) {
                    if (quantityObj instanceof Integer) {
                        quantity = (Integer) quantityObj;
                    } else if (quantityObj instanceof String) {
                        try {
                            quantity = Integer.parseInt((String) quantityObj);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid quantity format: " + quantityObj);
                        }
                    }
                }
                
                result = orderService.addToCart(accountId, productId, quantity);
            }
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add to cart: " + e.getMessage(), e);
        }
    }

    @PostMapping("/restore-cart-quantities/{cartOrderId}")
    public ResponseEntity<Map<String, String>> restoreCartQuantities(@PathVariable String cartOrderId) {
        // Manual endpoint to restore quantities for a specific cart (useful for testing)
        try {
            orderService.restoreCartProductQuantities(cartOrderId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cart quantities restored for order: " + cartOrderId);
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to restore cart quantities: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
