package com.pulseras.api.controller;

import com.pulseras.api.dto.CreateOrderDTO;
import com.pulseras.api.dto.OrderDTO;
import com.pulseras.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/{id}")
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

    @GetMapping("/weekly-overview")
    public ResponseEntity<?> getWeeklyOverview() {
        return ResponseEntity.ok(orderService.getWeeklyOverview());
    }

}
