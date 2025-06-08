package com.pulseras.api.service.impl;

import com.pulseras.api.dto.CreateOrderDTO;
import com.pulseras.api.dto.OrderDTO;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.OrderMapper;
import com.pulseras.api.entity.Order;
import com.pulseras.api.repository.OrderRepository;
import com.pulseras.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

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
        Order order = OrderMapper.toEntity(dto);
        return OrderMapper.toDTO(orderRepository.save(order));
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
}
