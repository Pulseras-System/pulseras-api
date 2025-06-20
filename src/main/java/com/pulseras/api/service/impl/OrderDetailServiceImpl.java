package com.pulseras.api.service.impl;

import com.pulseras.api.dto.CreateOrderDetailDTO;
import com.pulseras.api.dto.OrderDetailDTO;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.OrderDetailMapper;
import com.pulseras.api.entity.OrderDetail;
import com.pulseras.api.repository.OrderDetailRepository;
import com.pulseras.api.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository repository;

    @Override
    public List<OrderDetailDTO> getAllOrderDetails() {
        return repository.findAll().stream()
                .map(OrderDetailMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDetailDTO getOrderDetailById(String id) {
        OrderDetail entity = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Order Detail not found with id: " + id));
        return OrderDetailMapper.toDTO(entity);
    }

    @Override
    public OrderDetailDTO createOrderDetail(CreateOrderDetailDTO dto) {
        OrderDetail entity = OrderDetailMapper.toEntity(dto);
        return OrderDetailMapper.toDTO(repository.save(entity));
    }

    @Override
    public OrderDetailDTO updateOrderDetail(String id, CreateOrderDetailDTO dto) {
        OrderDetail existing = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Order Detail not found with id: " + id));

        existing.setOrderId(dto.getOrderId());
        existing.setProductId(dto.getProductId());
        existing.setQuantity(dto.getQuantity());
        existing.setPrice(dto.getPrice());
        existing.setPromotionId(dto.getPromotionId());
        existing.setStatus(dto.getStatus());
        existing.setLastEdited(dto.getLastEdited());

        return OrderDetailMapper.toDTO(repository.save(existing));
    }

    @Override
    public void deleteOrderDetail(String id) {
        ObjectId objId = new ObjectId(id);
        if (!repository.existsById(objId)) {
            throw new ResourceNotFoundException("Order Detail not found with id: " + id);
        }
        repository.deleteById(objId);
    }

    @Override
    public OrderDetailDTO getOrderDetailByOrderId(String orderId) {
        OrderDetail orderDetail = repository.findFirstByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Detail not found for order ID: " + orderId));
        return OrderDetailMapper.toDTO(orderDetail);
    }

    @Override
    public List<OrderDetailDTO> getAllOrderDetailsByOrderId(String orderId){
        List<OrderDetail> orderDetails = repository.findByOrderId(orderId);
        return orderDetails.stream().map(OrderDetailMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public int countOrderDetailsByOrderId(String orderId){
        return (int) repository.findByOrderId(orderId).stream()
                .filter(orderDetail -> Objects.equals(orderDetail.getStatus(), 1))
                .count();
    }
}
