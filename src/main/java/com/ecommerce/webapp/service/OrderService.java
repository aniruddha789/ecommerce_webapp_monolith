package com.ecommerce.webapp.service;

import com.ecommerce.webapp.entity.ShopOrder;
import com.ecommerce.webapp.entity.OrderItem;
import com.ecommerce.webapp.entity.OrderStatus;
import com.ecommerce.webapp.entity.UserEntity;
import com.ecommerce.webapp.repository.ShopOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.ecommerce.webapp.exception.InvalidOrderStateException;
import com.ecommerce.webapp.exception.OrderNotFoundException;

@Service
public class OrderService {

    @Autowired
    ShopOrderRepository shopOrderRepository;

    public ShopOrder getOrCreateCart(UserEntity user) {
        Optional<ShopOrder> existingCart = shopOrderRepository.findByUserAndOrderStatus(user, OrderStatus.CART);
        if (existingCart.isPresent()) {
            return existingCart.get();
        } else {
            ShopOrder newCart = new ShopOrder(user, LocalDateTime.now());
            return shopOrderRepository.save(newCart);
        }
    }

    public void addItemToCart(UserEntity user, OrderItem item) {
        ShopOrder cart = getOrCreateCart(user);
        cart.getOrderItems().add(item);
        shopOrderRepository.save(cart);
    }

    public void removeItemFromCart(UserEntity user, int itemId) {
        ShopOrder cart = getOrCreateCart(user);
        cart.getOrderItems().removeIf(item -> item.getId() == itemId);
        shopOrderRepository.save(cart);
    }

    public void updateItemQuantity(UserEntity user, int itemId, int newQuantity) {
        ShopOrder cart = getOrCreateCart(user);
        cart.getOrderItems().stream()
            .filter(item -> item.getId() == itemId)
            .findFirst()
            .ifPresent(item -> item.setQuantity(newQuantity));
        shopOrderRepository.save(cart);
    }

    public ShopOrder checkout(UserEntity user) {
        ShopOrder cart = getOrCreateCart(user);
        cart.setOrderStatus(OrderStatus.PLACED);
        cart.setOrderDate(LocalDateTime.now());
        return shopOrderRepository.save(cart);
    }

    public ShopOrder getOrderById(int orderId) {
        return shopOrderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
    }

    public List<ShopOrder> getUserOrders(UserEntity user) {
        return shopOrderRepository.findByUserAndOrderStatusNot(user, OrderStatus.CART);
    }

    public ShopOrder cancelOrder(int orderId) {
        ShopOrder order = getOrderById(orderId);
        if (order.getOrderStatus() == OrderStatus.PLACED) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            return shopOrderRepository.save(order);
        }
        throw new InvalidOrderStateException("Cannot cancel order with status: " + order.getOrderStatus());
    }
}
