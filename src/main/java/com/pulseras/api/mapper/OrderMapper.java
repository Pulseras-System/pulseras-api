package com.pulseras.api.mapper;

import com.pulseras.api.dto.CreateOrderDTO;
import com.pulseras.api.dto.OrderDTO;
import com.pulseras.api.dto.OrderDetailDTO;
import com.pulseras.api.entity.Account;
import com.pulseras.api.entity.Category;
import com.pulseras.api.entity.Order;
import com.pulseras.api.repository.AccountRepository;
import com.pulseras.api.repository.CategoryRepository;
import com.pulseras.api.repository.OrderDetailRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private static OrderDetailRepository orderDetailRepository;

    private static AccountRepository accountRepository;

    @Autowired
    public OrderMapper(AccountRepository accountRepository) {
        OrderMapper.accountRepository = accountRepository;
    }

    @Autowired
    public void setOrderDetailRepository(OrderDetailRepository orderDetailRepository) {
        OrderMapper.orderDetailRepository = orderDetailRepository;
    }

    public static OrderDTO toDTO(Order entity) {
        // Get order details for this order
        List<OrderDetailDTO> orderDetails = orderDetailRepository.findByOrderId(entity.getId().toHexString())
                .stream()
                .map(OrderDetailMapper::toDTO)
                .collect(Collectors.toList());

        String fullName = "";
        if (entity.getAccountId() != null) {
            fullName = accountRepository.findById(new ObjectId(entity.getAccountId()))
                    .map(Account::getFullName)
                    .orElse("");
        }

        return OrderDTO.builder()
                .id(entity.getId().toHexString())
                .orderInfor(entity.getOrderInfor())
                .amount(entity.getAmount())
                .accountId(entity.getAccountId())
                .voucherId(entity.getVoucherId())
                .totalPrice(entity.getTotalPrice())
                .status(entity.getStatus())
                .lastEdited(entity.getLastEdited())
                .createDate(entity.getCreateDate())
                .orderDetails(orderDetails)
                .fullName(fullName)
                .build();
    }

    public static Order toEntity(CreateOrderDTO dto) {
        return Order.builder()
                .id(new ObjectId())
                .orderInfor(dto.getOrderInfor())
                .amount(dto.getAmount())
                .accountId(dto.getAccountId())
                .voucherId(dto.getVoucherId())
                .totalPrice(dto.getTotalPrice())
                .status(dto.getStatus())
                .lastEdited(dto.getLastEdited())
                .createDate(LocalDateTime.now())
                .build();
    }
}
