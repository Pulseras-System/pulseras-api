package com.pulseras.api.service.impl;

import com.pulseras.api.entity.Order;
import com.pulseras.api.entity.OrderDetail;
import com.pulseras.api.entity.Product;
import com.pulseras.api.repository.OrderRepository;
import com.pulseras.api.repository.OrderDetailRepository;
import com.pulseras.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartCleanupService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    /**
     * Automatically clean up expired cart items (status = 1) that haven't been 
     * updated for more than 30 days. Set their status to 0 and restore product quantities.
     * Runs daily at 2 AM.
     */
    @Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
    @Transactional
    public void cleanupExpiredCarts() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            
            // Find all cart orders (status = 1) that haven't been updated in 30 days
            List<Order> expiredCarts = orderRepository.findAll().stream()
                    .filter(order -> order.getStatus() != null && order.getStatus() == 1)
                    .filter(order -> order.getLastEdited() != null && order.getLastEdited().isBefore(cutoffDate))
                    .toList();
            
            int processedCarts = 0;
            int restoredItems = 0;
            double restoredQuantity = 0;
            
            for (Order cart : expiredCarts) {
                // Get all active cart items
                List<OrderDetail> activeCartItems = orderDetailRepository.findByOrderId(cart.getId().toString())
                        .stream()
                        .filter(detail -> detail.getStatus() != null && detail.getStatus() == 1)
                        .toList();
                
                // Restore product quantities and deactivate cart items
                for (OrderDetail cartItem : activeCartItems) {
                    // Restore product quantity
                    restoreProductQuantity(cartItem.getProductId(), cartItem.getQuantity());
                    
                    // Deactivate cart item
                    cartItem.setStatus(0);
                    cartItem.setLastEdited(LocalDateTime.now());
                    orderDetailRepository.save(cartItem);
                    
                    restoredItems++;
                    restoredQuantity += cartItem.getQuantity();
                }
                
                // Update cart status to expired/inactive
                cart.setStatus(0);
                cart.setLastEdited(LocalDateTime.now());
                cart.setAmount(0);
                cart.setTotalPrice(0.0);
                orderRepository.save(cart);
                
                processedCarts++;
            }
            
            if (processedCarts > 0) {
                System.out.println("Cart cleanup completed: " +
                        "Processed " + processedCarts + " expired carts, " +
                        "restored " + restoredItems + " items, " +
                        "total quantity restored: " + restoredQuantity);
            }
            
        } catch (Exception e) {
            System.err.println("Error during cart cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Clean up cart items that have been inactive for 1 month but cart is still active.
     * This handles edge cases where the cart is still being used but some items are very old.
     */
    @Scheduled(cron = "0 30 2 * * *") // Daily at 2:30 AM
    @Transactional
    public void cleanupExpiredCartItems() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            
            // Find all active cart items that are older than 30 days
            List<OrderDetail> expiredCartItems = orderDetailRepository.findAll().stream()
                    .filter(detail -> detail.getStatus() != null && detail.getStatus() == 1)
                    .filter(detail -> detail.getLastEdited() != null && detail.getLastEdited().isBefore(cutoffDate))
                    .filter(detail -> {
                        // Check if this belongs to a cart order
                        if (detail.getOrderId() != null) {
                            Order order = orderRepository.findById(new org.bson.types.ObjectId(detail.getOrderId())).orElse(null);
                            return order != null && order.getStatus() != null && order.getStatus() == 1;
                        }
                        return false;
                    })
                    .toList();
            
            int cleanedItems = 0;
            double restoredQuantity = 0;
            
            for (OrderDetail cartItem : expiredCartItems) {
                // Restore product quantity
                restoreProductQuantity(cartItem.getProductId(), cartItem.getQuantity());
                
                // Deactivate cart item
                cartItem.setStatus(0);
                cartItem.setLastEdited(LocalDateTime.now());
                orderDetailRepository.save(cartItem);
                
                // Update cart totals
                updateCartTotals(cartItem.getOrderId());
                
                cleanedItems++;
                restoredQuantity += cartItem.getQuantity();
            }
            
            if (cleanedItems > 0) {
                System.out.println("Cart item cleanup completed: " +
                        "Cleaned " + cleanedItems + " expired items, " +
                        "total quantity restored: " + restoredQuantity);
            }
            
        } catch (Exception e) {
            System.err.println("Error during cart item cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to restore product quantity
     */
    private void restoreProductQuantity(String productId, Integer quantity) {
        try {
            if (productId != null && quantity != null && quantity > 0) {
                Product product = productRepository.findById(productId).orElse(null);
                if (product != null) {
                    product.setQuantity(product.getQuantity() + quantity);
                    product.setLastEdited(LocalDateTime.now());
                    productRepository.save(product);
                    System.out.println("Restored " + quantity + " units for product " + productId + 
                                     ". New stock: " + product.getQuantity());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to restore product quantity for " + productId + ": " + e.getMessage());
        }
    }
    
    /**
     * Helper method to update cart totals after cleanup
     */
    private void updateCartTotals(String orderId) {
        try {
            Order order = orderRepository.findById(new org.bson.types.ObjectId(orderId)).orElse(null);
            if (order == null) return;
            
            // Get all active order details for this order
            List<OrderDetail> activeDetails = orderDetailRepository.findByOrderId(orderId)
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
            order.setLastEdited(LocalDateTime.now());
            
            orderRepository.save(order);
        } catch (Exception e) {
            System.err.println("Failed to update cart totals for orderId: " + orderId + ", error: " + e.getMessage());
        }
    }
}
