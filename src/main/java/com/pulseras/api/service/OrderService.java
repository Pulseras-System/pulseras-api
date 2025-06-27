package com.pulseras.api.service;

import com.pulseras.api.dto.CreateOrderDTO;
import com.pulseras.api.dto.OrderDTO;

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
    List<Map<String, Object>> getWeeklyOverview();
    List<Map<String, Object>> getMonthlyOverview();
    List<Map<String, Object>> getYearlyOverview();
}
