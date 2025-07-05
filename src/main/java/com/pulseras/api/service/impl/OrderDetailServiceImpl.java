package com.pulseras.api.service.impl;

import com.pulseras.api.dto.CreateOrderDetailDTO;
import com.pulseras.api.dto.OrderDetailDTO;
import com.pulseras.api.dto.UpdateOrderDetailDTO;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.OrderDetailMapper;
import com.pulseras.api.entity.OrderDetail;
import com.pulseras.api.entity.Order;
import com.pulseras.api.entity.Product;
import com.pulseras.api.repository.OrderDetailRepository;
import com.pulseras.api.repository.OrderRepository;
import com.pulseras.api.repository.ProductRepository;
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
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

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
        // Check if this is for a cart order (status = 1)
        if (dto.getOrderId() != null) {
            Order order = orderRepository.findById(new ObjectId(dto.getOrderId())).orElse(null);
            
            if (order != null && order.getStatus() != null && order.getStatus() == 1) {
                // This is a cart order - handle cart logic
                return handleCartOrderDetail(dto, order);
            }
        }
        
        // Create new order detail for non-cart orders
        OrderDetail entity = OrderDetailMapper.toEntity(dto);
        entity.setCreateDate(java.time.LocalDateTime.now());
        return OrderDetailMapper.toDTO(repository.save(entity));
    }
    
    // Helper method to handle cart order detail creation/update
    private OrderDetailDTO handleCartOrderDetail(CreateOrderDetailDTO dto, Order cartOrder) {
        // Get product information for price and stock management
        Product product = null;
        if (dto.getProductId() != null) {
            product = productRepository.findById(dto.getProductId()).orElse(null);
            if (product == null) {
                throw new ResourceNotFoundException("Product not found with id: " + dto.getProductId());
            }
        }
        
        // Use product price if dto price is null or 0
        Double effectivePrice = dto.getPrice();
        if ((effectivePrice == null || effectivePrice == 0.0) && product != null) {
            effectivePrice = product.getPrice().doubleValue();
        }
        
        // Default quantity to 1 if not specified for cart operations
        Integer effectiveQuantity = dto.getQuantity();
        if (effectiveQuantity == null || effectiveQuantity <= 0) {
            effectiveQuantity = 1;
        }
        
        // Check product availability
        if (product != null && product.getQuantity() < effectiveQuantity) {
            throw new IllegalStateException("Insufficient stock for product: " + product.getProductName() + 
                                          ". Available: " + product.getQuantity() + ", Requested: " + effectiveQuantity);
        }
        
        // Check for existing product in cart
        List<OrderDetail> existingDetails = repository.findByOrderId(dto.getOrderId());
        OrderDetail existingDetail = existingDetails.stream()
                .filter(detail -> dto.getProductId().equals(detail.getProductId()))
                .findFirst()
                .orElse(null);
        
        if (existingDetail != null) {
            // Product already exists in cart
            if (existingDetail.getStatus() == 0) {
                // Reactivate deleted item and set new quantity
                existingDetail.setStatus(1);
                existingDetail.setQuantity(effectiveQuantity);
                System.out.println("Reactivated product " + dto.getProductId() + " in cart " + cartOrder.getId());
            } else {
                // Increment existing quantity by the specified amount
                int oldQuantity = existingDetail.getQuantity();
                existingDetail.setQuantity(oldQuantity + effectiveQuantity);
                System.out.println("Updated product " + dto.getProductId() + " quantity from " + oldQuantity + " to " + (oldQuantity + effectiveQuantity));
            }
            
            // Update price if it was provided or looked up
            if (effectivePrice != null) {
                existingDetail.setPrice(effectivePrice);
            }
            
            // Decrease product stock
            if (product != null) {
                product.setQuantity(product.getQuantity() - effectiveQuantity);
                product.setLastEdited(java.time.LocalDateTime.now());
                productRepository.save(product);
                System.out.println("Product stock decreased by " + effectiveQuantity + " to: " + product.getQuantity());
            }
            
            existingDetail.setLastEdited(java.time.LocalDateTime.now());
            OrderDetail saved = repository.save(existingDetail);
            
            // Update order totals
            updateOrderTotals(dto.getOrderId());
            
            return OrderDetailMapper.toDTO(saved);
        } else {
            // Create new order detail with proper defaults
            OrderDetail newDetail = OrderDetail.builder()
                    .orderId(dto.getOrderId())
                    .productId(dto.getProductId())
                    .quantity(effectiveQuantity)
                    .price(effectivePrice)
                    .promotionId(dto.getPromotionId() != null ? dto.getPromotionId() : 0)
                    .status(1) // Active by default for cart items
                    .createDate(java.time.LocalDateTime.now())
                    .lastEdited(java.time.LocalDateTime.now())
                    .build();
            
            // Decrease product stock
            if (product != null) {
                product.setQuantity(product.getQuantity() - effectiveQuantity);
                product.setLastEdited(java.time.LocalDateTime.now());
                productRepository.save(product);
                System.out.println("Product stock decreased by " + effectiveQuantity + " to: " + product.getQuantity());
            }
            
            OrderDetail saved = repository.save(newDetail);
            System.out.println("Added new product " + dto.getProductId() + " to cart " + cartOrder.getId() + " with quantity " + effectiveQuantity);
            
            // Update order totals
            updateOrderTotals(dto.getOrderId());
            
            return OrderDetailMapper.toDTO(saved);
        }
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

        OrderDetail saved = repository.save(existing);
        
        // If this is a cart order, update totals
        if (dto.getOrderId() != null) {
            Order order = orderRepository.findById(new ObjectId(dto.getOrderId())).orElse(null);
            if (order != null && order.getStatus() != null && order.getStatus() == 1) {
                updateOrderTotals(dto.getOrderId());
            }
        }
        
        return OrderDetailMapper.toDTO(saved);
    }

    @Override
    public void deleteOrderDetail(String id) {
        ObjectId objId = new ObjectId(id);
        OrderDetail orderDetail = repository.findById(objId).orElse(null);
        
        if (orderDetail == null) {
            throw new ResourceNotFoundException("Order Detail not found with id: " + id);
        }
        
        // Check if this is a cart item and restore product quantity
        if (orderDetail.getOrderId() != null) {
            Order order = orderRepository.findById(new ObjectId(orderDetail.getOrderId())).orElse(null);
            if (order != null && order.getStatus() != null && order.getStatus() == 1) {
                // This is a cart item - restore product quantity
                restoreProductQuantity(orderDetail.getProductId(), orderDetail.getQuantity());
                
                // Update cart totals after removal
                repository.deleteById(objId);
                updateOrderTotals(orderDetail.getOrderId());
                return;
            }
        }
        
        // Regular order detail deletion
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
    @Override
    public OrderDetailDTO partialUpdateOrderDetail(String id, UpdateOrderDetailDTO dto) {
        OrderDetail existing = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Order Detail not found with id: " + id));

        if (dto.getProductId() != null) existing.setProductId(dto.getProductId());
        if (dto.getQuantity() != null) existing.setQuantity(dto.getQuantity());
        if (dto.getPrice() != null) existing.setPrice(dto.getPrice());
        if (dto.getPromotionId() != null) existing.setPromotionId(dto.getPromotionId());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        if (dto.getLastEdited() != null) {
            existing.setLastEdited(dto.getLastEdited());
        } else {
            existing.setLastEdited(java.time.LocalDateTime.now());
        }

        OrderDetail saved = repository.save(existing);
        
        // If this is a cart order, update totals
        if (existing.getOrderId() != null) {
            Order order = orderRepository.findById(new ObjectId(existing.getOrderId())).orElse(null);
            if (order != null && order.getStatus() != null && order.getStatus() == 1) {
                updateOrderTotals(existing.getOrderId());
            }
        }

        return OrderDetailMapper.toDTO(saved);
    }
    
    // Helper method to update order totals for cart orders
    private void updateOrderTotals(String orderId) {
        try {
            Order order = orderRepository.findById(new ObjectId(orderId)).orElse(null);
            if (order == null) return;
            
            // Get all active order details for this order
            List<OrderDetail> activeDetails = repository.findByOrderId(orderId)
                    .stream()
                    .filter(detail -> detail.getStatus() != null && detail.getStatus() == 1)
                    .toList();
            
            // Calculate totals
            int totalAmount = activeDetails.stream()
                    .mapToInt(detail -> detail.getQuantity() != null ? detail.getQuantity() : 0)
                    .sum();
            
            double totalPrice = activeDetails.stream()
                    .mapToDouble(detail -> {
                        Double price = detail.getPrice();
                        Integer quantity = detail.getQuantity();
                        return (price != null ? price : 0.0) * (quantity != null ? quantity : 0);
                    })
                    .sum();
            
            // Update order
            order.setAmount(totalAmount);
            order.setTotalPrice(totalPrice);
            order.setLastEdited(java.time.LocalDateTime.now());
            
            orderRepository.save(order);
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Failed to update order totals for orderId: " + orderId + ", error: " + e.getMessage());
        }
    }
    
    // Helper method to restore product quantity when cart item is removed
    private void restoreProductQuantity(String productId, Integer quantity) {
        try {
            if (productId != null && quantity != null && quantity > 0) {
                Product product = productRepository.findById(productId).orElse(null);
                if (product != null) {
                    product.setQuantity(product.getQuantity() + quantity);
                    product.setLastEdited(java.time.LocalDateTime.now());
                    productRepository.save(product);
                    System.out.println("Restored product quantity for " + productId + " by " + quantity + 
                                     ". New stock: " + product.getQuantity());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to restore product quantity for " + productId + ": " + e.getMessage());
        }
    }
}
