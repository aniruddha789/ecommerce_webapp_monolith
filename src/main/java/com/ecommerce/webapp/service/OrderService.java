package com.ecommerce.webapp.service;

import com.ecommerce.webapp.dto.request.order.SubmitOrderItem;
import com.ecommerce.webapp.dto.request.order.SubmitOrderRequest;
import com.ecommerce.webapp.entity.*;
import com.ecommerce.webapp.repository.OrderItemRepository;
import com.ecommerce.webapp.repository.ProductRepository;
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

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserService userService;

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

    public ShopOrder checkout(SubmitOrderRequest request) {
        if(sanitizeOrderRequest(request)) {
            ShopOrder cart;
            try {
                UserEntity user = userService.findByUsername(request.getUsername());
                cart = getOrCreateCart(user);
                cart = shopOrderRepository.save(cart);
                cart = updateCart(cart, request);
                if(cart != null) {
                    cart.setOrderStatus(OrderStatus.PLACED);
                    cart.setOrderDate(LocalDateTime.now());
                    cart.setUser(user);
                    return shopOrderRepository.save(cart);
                }
            } catch (Exception e){
                throw new InvalidOrderStateException(e.getMessage());
            }

            return ShopOrder.builder().orderStatus(OrderStatus.CANCELLED).build();

        } else {
            throw new InvalidOrderStateException("Invalid order request");
        }
    }

    protected ShopOrder updateCart(ShopOrder cart, SubmitOrderRequest request) {

        if(cart != null && request != null && !request.getItems().isEmpty()){

            for(SubmitOrderItem item : request.getItems()){

                Product product = this.productRepository.findById(item.getId());
                if(product != null ) {
                    OrderItem orderItem = OrderItem.builder()
                            .orderID(cart.getId())
                            .productID(item.getId())
                            .color(item.getColor())
                            .quantity(item.getQuantity())
                            .size(item.getSize())
                            .build();

                    OrderItem savedOrderItem = this.orderItemRepository.save(orderItem);

                    cart.getOrderItems().add(savedOrderItem);
                }
            }

            return cart;
        }

        return null;
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

    protected boolean sanitizeOrderRequest(SubmitOrderRequest request){

        if(request.getUsername() == null || request.getUsername().isEmpty() || request.getItems().isEmpty()){
            throw new InvalidOrderStateException("Invalid order request username or items should not be empty");
        }

        return true;

    }
}
