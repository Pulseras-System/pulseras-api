package com.pulseras.api.service.impl;

import com.pulseras.api.dto.OrderDto;
import com.pulseras.api.entity.Order;
import com.pulseras.api.mapper.OrderMapper;
import com.pulseras.api.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepo repo;

    public List<OrderDto> findAll() {
        return repo.findAll().stream().map(OrderMapper::toDto).collect(Collectors.toList());
    }

    public OrderDto findById(String id) {
        return repo.findById(id).map(OrderMapper::toDto).orElse(null);
    }

    public OrderDto create(OrderDto dto) {
        Order saved = repo.save(OrderMapper.toEntity(dto));
        return OrderMapper.toDto(saved);
    }

    public OrderDto update(String id, OrderDto dto) {
        Order existing = repo.findById(id).orElseThrow();
        Order updated = OrderMapper.toEntity(dto);
        updated.setId(id);
        return OrderMapper.toDto(repo.save(updated));
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}
