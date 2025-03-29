package com.ecommerce.webapp.controller;

import com.ecommerce.webapp.dto.request.order.SubmitOrderItem;
import com.ecommerce.webapp.dto.request.order.SubmitOrderRequest;
import com.ecommerce.webapp.dto.response.order.CartIconResponse;
import com.ecommerce.webapp.dto.response.order.OrderResponse;
import com.ecommerce.webapp.entity.OrderItem;
import com.ecommerce.webapp.entity.ShopOrder;
import com.ecommerce.webapp.entity.UserEntity;
import com.ecommerce.webapp.service.OrderService;
import com.ecommerce.webapp.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;


    @GetMapping("/cartIcon/{username}")
    public ResponseEntity<CartIconResponse> getCartIcon(@PathVariable String username) {
        UserEntity user = userService.findByUsername(username);
        CartIconResponse cartIconResponse = orderService.getCartIcon(user);
        return ResponseEntity.ok(cartIconResponse);
    }

    @GetMapping("/cart")
    public ResponseEntity<ShopOrder> getCart(@RequestParam String username) {
        UserEntity user = userService.findByUsername(username);
        ShopOrder cart = orderService.getOrCreateCart(user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/cart/add")
    public ResponseEntity<ShopOrder> addItemToCart(@RequestBody SubmitOrderRequest request) {
        ShopOrder order = orderService.addItemToCart(request);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/cart/remove")
    public ResponseEntity<ShopOrder> removeItemFromCart(@RequestParam String username, @RequestParam int itemId) {
        UserEntity user = userService.findByUsername(username);
        orderService.removeItemFromCart(user, itemId);
        return ResponseEntity.ok(orderService.getOrCreateCart(user));
    }

    @PostMapping("/cart/update")
    public ResponseEntity<ShopOrder> updateItemQuantity(@RequestParam String username, @RequestParam int itemId, @RequestParam int newQuantity) {
        UserEntity user = userService.findByUsername(username);
        orderService.updateItemQuantity(user, itemId, newQuantity);
        return ResponseEntity.ok(orderService.getOrCreateCart(user));
    }

    @PostMapping("/submitOrder")
    public ResponseEntity<ShopOrder> submitOrder(@RequestBody SubmitOrderRequest request) {
        ShopOrder orderStatus = orderService.submitOrder(request);
        return ResponseEntity.ok(orderStatus);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ShopOrder> getOrder(@PathVariable int orderId) {
        ShopOrder order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/getOrders/{username}")
    public ResponseEntity<OrderResponse> getUserOrders(@PathVariable String username) {
        UserEntity user = userService.findByUsername(username);
        OrderResponse order = orderService.getUserOrders(user);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ShopOrder> cancelOrder(@PathVariable int orderId) {
        ShopOrder cancelledOrder = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(cancelledOrder);
    }
}
