package com.ecommerce.webapp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ecommerce.webapp.dto.response.order.CartIconResponse;
import com.ecommerce.webapp.dto.response.order.OrderResponse;
import com.ecommerce.webapp.dto.response.order.OrderResponseOrder;
import com.ecommerce.webapp.dto.response.order.OrdersResponseOrderItem;
import com.ecommerce.webapp.entity.*;
import com.ecommerce.webapp.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.webapp.dto.request.order.SubmitOrderItem;
import com.ecommerce.webapp.dto.request.order.SubmitOrderRequest;
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
    InventoryRepository inventoryRepository;

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

    /** Basically adds all items to cart */
    public ShopOrder addItemToCart(SubmitOrderRequest request) {
        if(sanitizeOrderRequest(request)) {
            ShopOrder cart;
            try {
                UserEntity user = userService.findByUsername(request.getUsername());
                cart = getOrCreateCart(user);
                cart = shopOrderRepository.save(cart);
                cart = updateCart(cart, request);
                if(cart != null) {
                    /** Only this line is different from submitOrder function */
                    cart.setOrderStatus(OrderStatus.CART);
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

    public ShopOrder submitOrder(SubmitOrderRequest request) {
        if(sanitizeOrderRequest(request)) {
            ShopOrder cart;
            try {
                UserEntity user = userService.findByUsername(request.getUsername());
                cart = getOrCreateCart(user);

                if(cart != null && processOrder(cart)) {
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

    @Transactional
    public boolean processOrder(ShopOrder order) {

        if ( order != null ) {

            for (OrderItem item : order.getOrderItems()) {

                Product product = this.productRepository.findById(item.getProductID());
                if ( product != null ) {

                    Inventory inventory = product.getInventory().stream().filter(a -> a.getColor().equalsIgnoreCase(item.getColor())
                    && a.getSize().equalsIgnoreCase(item.getSize())).findFirst().orElse(null);

                    if(inventory == null ) {
                        throw new InvalidOrderStateException("Inventory not found for product ID: " + item.getProductID());
                    } else if (inventory.getQuantity() < item.getQuantity()) {
                        throw new InvalidOrderStateException("Insufficient inventory for product ID: " + item.getProductID());
                    }

                    inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
                    this.inventoryRepository.save(inventory);

                    return true;
                }
            }
            return true;
        }

        return false;
    }

    protected ShopOrder updateCart(ShopOrder cart, SubmitOrderRequest request) {

        if(cart != null && request != null && !request.getItems().isEmpty()){

            for(SubmitOrderItem item : request.getItems()){

                Product product = this.productRepository.findById(item.getId());
                if ( product != null ) {

                    OrderItem savedOrderItem = null;

                    OrderItem existingOrderItem = cart.getOrderItems().stream().filter(a -> a.getProductID() == item.getId()
                            && a.getColor().equals(item.getColor())
                            && a.getSize().equals(item.getSize())
                    ).findFirst().orElse(null);

                    if(existingOrderItem != null) {
                        existingOrderItem.setQuantity(existingOrderItem.getQuantity() + item.getQuantity());
                        savedOrderItem = this.orderItemRepository.save(existingOrderItem);

                        /** Update item */
                        int index = cart.getOrderItems().indexOf(existingOrderItem);
                        if(index > -1) {
                            cart.getOrderItems().set(index, savedOrderItem);
                        }

                    } else {
                        OrderItem orderItem = OrderItem.builder()
                                .orderID(cart.getId())
                                .productID(item.getId())
                                .color(item.getColor())
                                .quantity(item.getQuantity())
                                .size(item.getSize())
                                .build();

                        savedOrderItem = this.orderItemRepository.save(orderItem);
                        /** Add item to cart */
                        cart.getOrderItems().add(savedOrderItem);
                    }


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
