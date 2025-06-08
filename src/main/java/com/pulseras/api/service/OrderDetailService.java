package com.pulseras.api.service;

import com.pulseras.api.dto.CreateOrderDetailDTO;
import com.pulseras.api.dto.OrderDetailDTO;

import java.util.List;

public interface OrderDetailService {
    List<OrderDetailDTO> getAllOrderDetails();
    OrderDetailDTO getOrderDetailById(String id);
    OrderDetailDTO createOrderDetail(CreateOrderDetailDTO dto);
    OrderDetailDTO updateOrderDetail(String id, CreateOrderDetailDTO dto);
    void deleteOrderDetail(String id);
}
