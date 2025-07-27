package com.pulseras.api.controller;

import com.pulseras.api.dto.*;
import com.pulseras.api.service.OrderService;
import com.pulseras.api.service.EmailService;
import com.pulseras.api.service.AccountService;
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
    private final EmailService emailService;
    private final AccountService accountService;

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
    public ResponseEntity<OrderDTO> update(@PathVariable String id, @RequestBody UpdateOrderDTO dto) {
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
    public ResponseEntity<OrderDTO> addToCart(@RequestBody AddToCartDto request) {
        try {
//            String accountId = (String) request.get("accountId");
//            Object productIdObj = request.get("productId");
//            Object quantityObj = request.get("quantity");

            if (request.getAccountId() == null || request.getAccountId().trim().isEmpty()) {
                throw new IllegalArgumentException("AccountId is required");
            }

            if (request.getProductId() == null) {
                throw new IllegalArgumentException("ProductId is required");
            }

            var result = orderService.addToCart(request.getAccountId(), request.getProductId());

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

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable String id, @RequestBody StatusUpdateDTO statusUpdate) {
        try {
            OrderDTO order = orderService.getOrderById(id);
            
            // Update status
            UpdateOrderDTO updateDto = new UpdateOrderDTO();
            updateDto.setStatus(statusUpdate.getStatus());
            updateDto.setLastEdited(LocalDateTime.now());
            orderService.partialUpdateOrder(id, updateDto);
            
            // Prepare status message
            String message;
            switch (statusUpdate.getStatus()) {
                case 0:
                    message = "Đơn hàng #" + id + " đã bị hủy.";
                    if (statusUpdate.getReason() != null && !statusUpdate.getReason().trim().isEmpty()) {
                        message += " Lý do: " + statusUpdate.getReason();
                    }
                    break;
                case 1:
                    message = "Đơn hàng #" + id + " đã được tạo thành công.";
                    break;
                case 2:
                    message = "Đơn hàng #" + id + " đã được xác nhận và đang được xử lý.";
                    break;
                case 3:
                    message = "Đơn hàng #" + id + " đang được vận chuyển.";
                    break;
                case 4:
                    message = "Đơn hàng #" + id + " đã được giao thành công.";
                    break;
                default:
                    message = "Trạng thái đơn hàng #" + id + " đã được cập nhật.";
            }
            
            // Send notification email to customer
            try {
                AccountDTO account = accountService.getAccountById(order.getAccountId());
                emailService.sendEmail(account.getEmail(), message, id);
            } catch (Exception e) {
                // Log error but don't fail the status update
                System.err.println("Failed to send email notification: " + e.getMessage());
            }
            
            return ResponseEntity.ok("Cập nhật trạng thái đơn hàng thành công");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
    }

}
