package com.pulseras.api.service;

import com.pulseras.api.dto.AggregatedOverview;
import com.pulseras.api.dto.CreateOrderDTO;
import com.pulseras.api.dto.OrderDTO;
import com.pulseras.api.dto.UpdateOrderDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderService {
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(String id);
    OrderDTO createOrder(CreateOrderDTO dto);
    OrderDTO updateOrder(String id, CreateOrderDTO dto);
    void deleteOrder(String id);
    List<OrderDTO> getOrdersByAccountId(String accountId);
    Map<String, Object> totalRevenueWithCompare();
    Map<String, Object> totalOrdersWithCompare();
    Map<String, Object> totalGrowthWithCompare();
    AggregatedOverview getOverview();
    OrderDTO partialUpdateOrder(String id, UpdateOrderDTO dto);
    
    // Cart management methods
    void restoreCartProductQuantities(String cartOrderId);
    OrderDTO addToCart(String accountId, String productId);
    OrderDTO addMultipleToCart(String accountId, List<String> productIds);
}
