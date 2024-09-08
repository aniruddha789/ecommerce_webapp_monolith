package com.ecommerce.webapp.controller;

import com.ecommerce.webapp.entity.OrderItem;
import com.ecommerce.webapp.entity.ShopOrder;
import com.ecommerce.webapp.entity.UserEntity;
import com.ecommerce.webapp.service.OrderService;
import com.ecommerce.webapp.service.UserService;

import java.util.List;

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

    @GetMapping("/cart")
    public ResponseEntity<ShopOrder> getCart(@RequestParam String username) {
        UserEntity user = userService.findByUsername(username);
        ShopOrder cart = orderService.getOrCreateCart(user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/cart/add")
    public ResponseEntity<ShopOrder> addItemToCart(@RequestParam String username, @RequestBody OrderItem item) {
        UserEntity user = userService.findByUsername(username);
        orderService.addItemToCart(user, item);
        return ResponseEntity.ok(orderService.getOrCreateCart(user));
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

    @PostMapping("/checkout")
    public ResponseEntity<ShopOrder> checkout(@RequestParam String username) {
        UserEntity user = userService.findByUsername(username);
        ShopOrder placedOrder = orderService.checkout(user);
        return ResponseEntity.ok(placedOrder);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ShopOrder> getOrder(@PathVariable int orderId) {
        ShopOrder order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<ShopOrder>> getUserOrders(@PathVariable String username) {
        UserEntity user = userService.findByUsername(username);
        List<ShopOrder> orders = orderService.getUserOrders(user);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ShopOrder> cancelOrder(@PathVariable int orderId) {
        ShopOrder cancelledOrder = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(cancelledOrder);
    }
}
