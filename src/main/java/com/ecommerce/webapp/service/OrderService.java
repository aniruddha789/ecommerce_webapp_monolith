package com.ecommerce.webapp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ecommerce.webapp.dto.response.order.CartIconResponse;
import com.ecommerce.webapp.dto.response.order.OrderResponse;
import com.ecommerce.webapp.dto.response.order.OrderResponseOrder;
import com.ecommerce.webapp.dto.response.order.OrdersResponseOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.webapp.dto.request.order.SubmitOrderItem;
import com.ecommerce.webapp.dto.request.order.SubmitOrderRequest;
import com.ecommerce.webapp.entity.OrderItem;
import com.ecommerce.webapp.entity.OrderStatus;
import com.ecommerce.webapp.entity.Product;
import com.ecommerce.webapp.entity.ShopOrder;
import com.ecommerce.webapp.entity.UserEntity;
import com.ecommerce.webapp.exception.InvalidOrderStateException;
import com.ecommerce.webapp.exception.OrderNotFoundException;
import com.ecommerce.webapp.repository.OrderItemRepository;
import com.ecommerce.webapp.repository.ProductRepository;
import com.ecommerce.webapp.repository.ShopOrderRepository;

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

    public CartIconResponse getCartIcon(UserEntity user) {
        ShopOrder cart = getOrCreateCart(user);
        int quantity = 0;
        if(cart != null && cart.getOrderItems() != null) {
            quantity = cart.getOrderItems().size();
        }
        return CartIconResponse.builder()
                .quantity(String.valueOf(quantity))
                .build();
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
                    cart.setUserAndAddOrder(user);
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
                if ( product != null ) {
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

    public OrderResponse getUserOrders(UserEntity user) {
        List<ShopOrder> orders = shopOrderRepository.findByUserAndOrderStatusNot(user, OrderStatus.CART);

        OrderResponse orderResponse = generateOrdersResponse(orders);
        orderResponse.setUserId(user.getId());
        return orderResponse;
    }

    protected OrderResponse generateOrdersResponse(List<ShopOrder> orders) {

        List<OrderResponseOrder> responses = new ArrayList<>();
        if(orders != null && !orders.isEmpty()){

            for(ShopOrder shopOrder: orders){
                List<OrdersResponseOrderItem> orderItems = new ArrayList<>();
                if(shopOrder.getOrderItems() !=  null && !shopOrder.getOrderItems().isEmpty()){

                    for(OrderItem orderItem: shopOrder.getOrderItems()){
                        orderItems.add(OrdersResponseOrderItem.builder()
                                .id(orderItem.getId())
                                .name(orderItem.getProduct().getName())
                                .productId(orderItem.getProductID())
                                .color(orderItem.getColor())
                                .quantity(orderItem.getQuantity())
                                .size(orderItem.getSize())
                                .build());
                    }
                }

                OrderResponseOrder response = OrderResponseOrder.builder()
                    .id(shopOrder.getId())
                    .orderDate(shopOrder.getOrderDate())
                    .orderStatus(shopOrder.getOrderStatus())
                    .orderItems(orderItems)
                    .build();
                responses.add(response);
            }

        }

        return OrderResponse.builder()
                .orders(responses)
                .build();
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
