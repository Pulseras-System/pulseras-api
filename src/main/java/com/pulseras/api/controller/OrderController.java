package com.pulseras.api.controller;

import com.pulseras.api.dto.OrderDto;
import com.pulseras.api.service.impl.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    @GetMapping
    public List<OrderDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public OrderDto getById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    public OrderDto create(@RequestBody OrderDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public OrderDto update(@PathVariable String id, @RequestBody OrderDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
