package com.pulseras.api.service.impl;

import com.pulseras.api.dto.*;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.OrderMapper;
import com.pulseras.api.entity.Order;
import com.pulseras.api.repository.OrderRepository;
import com.pulseras.api.service.AccountService;
import com.pulseras.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public OrderDTO partialUpdateOrder(String id, UpdateOrderDTO dto) {
        Order existing = orderRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (dto.getOrderInfor() != null) existing.setOrderInfor(dto.getOrderInfor());
        if (dto.getAmount() != null) existing.setAmount(dto.getAmount());
        if (dto.getVoucherId() != null) existing.setVoucherId(dto.getVoucherId());
        if (dto.getTotalPrice() != null) existing.setTotalPrice(dto.getTotalPrice());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());

        existing.setLastEdited(java.time.LocalDateTime.now());

        return OrderMapper.toDTO(orderRepository.save(existing));
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
                .filter(order -> order.getStatus() != null && order.getStatus() != 0 && order.getStatus() != 1)
                .map(order -> BigDecimal.valueOf(order.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        // ✅ Doanh thu tuần này
        BigDecimal thisWeekRevenue = orderRepository.findByCreateDateBetween(startThisWeek, LocalDateTime.now())
                .stream()
                .filter(order -> order.getStatus() != null && order.getStatus() != 0 && order.getStatus() != 1)
                .map(order -> BigDecimal.valueOf(order.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ;

        // ✅ Doanh thu tuần trước
        BigDecimal lastWeekRevenue = orderRepository.findByCreateDateBetween(startLastWeek, endLastWeek)
                .stream()
                .filter(order -> order.getStatus() != null && order.getStatus() != 0 && order.getStatus() != 1)
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
                .filter(order -> order.getStatus() != null && order.getStatus() != 0 && order.getStatus() != 1)
                .count();

        // ✅ Tổng đơn hàng tuần này
        long thisWeekOrders = orderRepository.findByCreateDateBetween(startThisWeek, LocalDateTime.now())
                .stream()
                .filter(order -> order.getStatus() != null && order.getStatus() != 0 && order.getStatus() != 1)
                .count();

        // ✅ Tổng đơn hàng tuần trước
        long lastWeekOrders = orderRepository.findByCreateDateBetween(startLastWeek, endLastWeek)
                .stream()
                .filter(order -> order.getStatus() != null && order.getStatus() != 0 && order.getStatus() != 1)
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
    @Transactional(readOnly = true)
    public AggregatedOverview getOverview() {
        LocalDate today = LocalDate.now();

        return new AggregatedOverview(
                buildWeekly(today),
                buildMonthly(today),
                buildYearly(today.getYear())
        );
    }

    private List<DailyOverview> buildWeekly(LocalDate today) {
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        return IntStream.range(0, 7)
                .mapToObj(i -> {
                    LocalDate current = monday.plusDays(i);
                    return aggregatePerDay(current,
                            i == 6 ? "CN" : "T" + (i + 2));
                })
                .toList();
    }

    private List<DailyOverview> buildMonthly(LocalDate today) {
        YearMonth ym = YearMonth.from(today);

        return IntStream.rangeClosed(1, ym.lengthOfMonth())
                .mapToObj(day -> aggregatePerDay(ym.atDay(day),
                        String.valueOf(day)))
                .toList();
    }

    private List<MonthlyOverview> buildYearly(int year) {
        return IntStream.rangeClosed(1, 12)
                .mapToObj(m -> aggregatePerMonth(YearMonth.of(year, m),
                        "T" + m))
                .toList();
    }

    private DailyOverview aggregatePerDay(LocalDate date, String label) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.atTime(LocalTime.MAX);

        List<Order> orders = orderRepository
                .findByCreateDateBetween(start, end)
                .stream()
                .filter(o -> isFinished(o.getStatus()))
                .toList();

        long count = orders.size();
        BigDecimal revenue = orders.stream()
                .map(o -> BigDecimal.valueOf(o.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DailyOverview(label, count, revenue);
    }

    private MonthlyOverview aggregatePerMonth(YearMonth ym, String label) {
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end   = ym.atEndOfMonth().atTime(LocalTime.MAX);

        List<Order> orders = orderRepository
                .findByCreateDateBetween(start, end)
                .stream()
                .filter(o -> isFinished(o.getStatus()))
                .toList();

        long count = orders.size();
        BigDecimal revenue = orders.stream()
                .map(o -> BigDecimal.valueOf(o.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MonthlyOverview(label, count, revenue);
    }

    private boolean isFinished(Integer status) {
        return status != null && status != 0 && status != 1;
    }
}
