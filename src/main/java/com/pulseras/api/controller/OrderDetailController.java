package com.pulseras.api.controller;

import com.pulseras.api.dto.CreateOrderDetailDTO;
import com.pulseras.api.dto.OrderDetailDTO;
import com.pulseras.api.dto.UpdateOrderDetailDTO;
import com.pulseras.api.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService service;

    @GetMapping
    public List<OrderDetailDTO> getAll() {
        return service.getAllOrderDetails();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getOrderDetailById(id));
    }

    @PostMapping
    public ResponseEntity<OrderDetailDTO> create(@RequestBody CreateOrderDetailDTO dto) {
        return ResponseEntity.ok(service.createOrderDetail(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDetailDTO> update(@PathVariable String id, @RequestBody CreateOrderDetailDTO dto) {
        return ResponseEntity.ok(service.updateOrderDetail(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteOrderDetail(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/order/{orderId}")
    public List<OrderDetailDTO> getOrderDetailByOrderId(@PathVariable String orderId) {
        List<OrderDetailDTO> orderDetail = service.getAllOrderDetailsByOrderId(orderId);
        return orderDetail;
    }

    @GetMapping("/amount/{id}")
    public ResponseEntity<Integer> getAmountByOrderId(@PathVariable String id) {
        return ResponseEntity.ok(service.countOrderDetailsByOrderId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderDetailDTO> partialUpdate(@PathVariable String id, @RequestBody UpdateOrderDetailDTO dto) {
        return ResponseEntity.ok(service.partialUpdateOrderDetail(id, dto));
    }

}
