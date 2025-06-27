package com.pulseras.api.service.impl;

import com.pulseras.api.dto.CreateOrderDTO;
import com.pulseras.api.dto.OrderDTO;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.OrderMapper;
import com.pulseras.api.entity.Order;
import com.pulseras.api.repository.OrderRepository;
import com.pulseras.api.service.AccountService;
import com.pulseras.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final AccountService accountService;

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

    @Override
    public List<OrderDTO> getOrdersByAccountId(String accountId){
        return orderRepository.findByAccountId(accountId).stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> totalRevenueWithCompare() {
        LocalDate today = LocalDate.now();
        LocalDate startOfThisWeek = today.with(DayOfWeek.MONDAY);
        LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
        LocalDate endOfLastWeek = startOfThisWeek.minusDays(1);

        // Convert to LocalDateTime
        LocalDateTime startThisWeek = startOfThisWeek.atStartOfDay();
        LocalDateTime startLastWeek = startOfLastWeek.atStartOfDay();
        LocalDateTime endLastWeek = endOfLastWeek.atTime(LocalTime.MAX);

        // ✅ Tổng doanh thu toàn bộ lịch sử (status ≠ 0 && status ≠ 1)
        BigDecimal totalRevenue = orderRepository.findAll()
                .stream()
                .filter(order -> order.getStatus() != 0 && order.getStatus() != 1)
                .map(order -> BigDecimal.valueOf(order.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        // ✅ Doanh thu tuần này
        BigDecimal thisWeekRevenue = orderRepository.findByCreateDateBetween(startThisWeek, LocalDateTime.now())
                .stream()
                .filter(order -> order.getStatus() != 0 && order.getStatus() != 1)
                .map(order -> BigDecimal.valueOf(order.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ;

        // ✅ Doanh thu tuần trước
        BigDecimal lastWeekRevenue = orderRepository.findByCreateDateBetween(startLastWeek, endLastWeek)
                .stream()
                .filter(order -> order.getStatus() != 0 && order.getStatus() != 1)
                .map(order -> BigDecimal.valueOf(order.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ;

        // ✅ Tính phần trăm thay đổi
        double percentChange = 0;
        if (lastWeekRevenue.compareTo(BigDecimal.ZERO) > 0) {
            percentChange = thisWeekRevenue.subtract(lastWeekRevenue)
                    .divide(lastWeekRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        } else if (thisWeekRevenue.compareTo(BigDecimal.ZERO) > 0) {
            percentChange = 100;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalRevenue", totalRevenue);
        result.put("percentChange", percentChange);
        result.put("isIncrease", thisWeekRevenue.compareTo(lastWeekRevenue) >= 0);
        result.put("thisWeekRevenue", thisWeekRevenue);
        result.put("lastWeekRevenue", lastWeekRevenue);

        return result;
    }


    @Override
    public Map<String, Object> totalOrdersWithCompare() {
        LocalDate today = LocalDate.now();
        LocalDate startOfThisWeek = today.with(DayOfWeek.MONDAY);
        LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
        LocalDate endOfLastWeek = startOfThisWeek.minusDays(1);

        // Convert to LocalDateTime
        LocalDateTime startThisWeek = startOfThisWeek.atStartOfDay();
        LocalDateTime startLastWeek = startOfLastWeek.atStartOfDay();
        LocalDateTime endLastWeek = endOfLastWeek.atTime(LocalTime.MAX);

        // ✅ Tổng toàn bộ đơn hàng trong lịch sử (status ≠ 0 && status ≠ 1)
        long totalOrders = orderRepository.findAll()
                .stream()
                .filter(order -> order.getStatus() != 0 && order.getStatus() != 1)
                .count();

        // ✅ Tổng đơn hàng tuần này
        long thisWeekOrders = orderRepository.findByCreateDateBetween(startThisWeek, LocalDateTime.now())
                .stream()
                .filter(order -> order.getStatus() != 0 && order.getStatus() != 1)
                .count();

        // ✅ Tổng đơn hàng tuần trước
        long lastWeekOrders = orderRepository.findByCreateDateBetween(startLastWeek, endLastWeek)
                .stream()
                .filter(order -> order.getStatus() != 0 && order.getStatus() != 1)
                .count();

        // ✅ Tính phần trăm thay đổi
        double percentChange = 0;
        if (lastWeekOrders > 0) {
            percentChange = ((double)(thisWeekOrders - lastWeekOrders) / lastWeekOrders) * 100;
        } else if (thisWeekOrders > 0) {
            percentChange = 100;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalOrders", totalOrders);
        result.put("percentChange", percentChange);
        result.put("isIncrease", thisWeekOrders >= lastWeekOrders);
        result.put("thisWeekOrders", thisWeekOrders);
        result.put("lastWeekOrders", lastWeekOrders);

        return result;
    }

    @Override
    public Map<String, Object> totalGrowthWithCompare() {
        Map<String, Object> revenueData = totalRevenueWithCompare();
        Map<String, Object> orderData = totalOrdersWithCompare();
        Map<String, Object> customerData = accountService.totalCustomersWithCompare();

        double revenueChange = (double) revenueData.get("percentChange");
        double orderChange = (double) orderData.get("percentChange");
        double customerChange = (double) customerData.get("percentChange");

        // ✅ Trung bình phần trăm tăng trưởng
        double averageGrowth = (revenueChange + orderChange + customerChange) / 3;

        // ✅ Xác định có tăng hay không
        boolean isIncrease = averageGrowth >= 0;

        Map<String, Object> result = new HashMap<>();
        result.put("growthRate", averageGrowth);
        result.put("isIncrease", isIncrease);
        result.put("revenueChange", revenueChange);
        result.put("orderChange", orderChange);
        result.put("customerChange", customerChange);

        return result;
    }

    @Override
    public List<Map<String, Object>> getWeeklyOverview() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = monday.plusDays(i);
            LocalDateTime startOfDay = currentDay.atStartOfDay();
            LocalDateTime endOfDay = currentDay.atTime(LocalTime.MAX);

            long orderCount = orderRepository
                    .findByCreateDateBetween(startOfDay, endOfDay)
                    .stream()
                    .filter(order -> order.getStatus() != 0 && order.getStatus() != 1)
                    .count();

            BigDecimal revenue = orderRepository
                    .findByCreateDateBetween(startOfDay, endOfDay)
                    .stream()
                    .filter(order -> order.getStatus() != 0 && order.getStatus() != 1)
                    .map(order -> BigDecimal.valueOf(order.getTotalPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> dayData = new HashMap<>();
            String dayLabel = (i == 6) ? "CN" : "T" + (i + 2);
            dayData.put("day", dayLabel);
            dayData.put("orderCount", orderCount);
            dayData.put("revenue", revenue);

            result.add(dayData);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getMonthlyOverview() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        int daysInMonth = currentMonth.lengthOfMonth();

        List<Map<String, Object>> result = new ArrayList<>();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = currentMonth.atDay(day);
            LocalDateTime startOfDay = currentDate.atStartOfDay();
            LocalDateTime endOfDay = currentDate.atTime(LocalTime.MAX);

            long orderCount = orderRepository
                    .findByCreateDateBetween(startOfDay, endOfDay)
                    .stream()
                    .filter(order -> order.getStatus() != 0 && order.getStatus() != 1)
                    .count();

            BigDecimal revenue = orderRepository
                    .findByCreateDateBetween(startOfDay, endOfDay)
                    .stream()
                    .filter(order -> order.getStatus() != 0 && order.getStatus() != 1)
                    .map(order -> BigDecimal.valueOf(order.getTotalPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("day", currentDate.getDayOfMonth()); // 1 -> 31
            dayData.put("orderCount", orderCount);
            dayData.put("revenue", revenue);

            result.add(dayData);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getYearlyOverview() {
        int currentYear = Year.now().getValue();
        List<Map<String, Object>> result = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            YearMonth currentMonth = YearMonth.of(currentYear, month);
            LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

            long orderCount = orderRepository
                    .findByCreateDateBetween(startOfMonth, endOfMonth)
                    .stream()
                    .filter(order -> order.getStatus() != 0 && order.getStatus() != 1)
                    .count();

            BigDecimal revenue = orderRepository
                    .findByCreateDateBetween(startOfMonth, endOfMonth)
                    .stream()
                    .filter(order -> order.getStatus() != 0 && order.getStatus() != 1)
                    .map(order -> BigDecimal.valueOf(order.getTotalPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", "T" + month); // T1 -> T12
            monthData.put("orderCount", orderCount);
            monthData.put("revenue", revenue);

            result.add(monthData);
        }

        return result;
    }




}
