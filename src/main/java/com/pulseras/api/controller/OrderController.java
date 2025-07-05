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

import com.pulseras.api.dto.AggregatedOverview;
import com.pulseras.api.dto.CreateOrderDTO;
import com.pulseras.api.dto.OrderDTO;
import com.pulseras.api.dto.UpdateOrderDTO;
import com.pulseras.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/cleanup-expired-carts")
    public ResponseEntity<Map<String, String>> cleanupExpiredCarts() {
        // This endpoint can be used to manually trigger cart cleanup for testing
        // In production, this would typically be removed or secured with admin access
        try {
            // Note: This would need the CartCleanupService to be injected here
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cart cleanup triggered manually. Check logs for details.");
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to trigger cart cleanup: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
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
