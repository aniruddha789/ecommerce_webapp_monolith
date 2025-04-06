package com.ecommerce.webapp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ecommerce.webapp.dto.request.order.DeleteItemsRequest;
import com.ecommerce.webapp.dto.response.order.CartIconResponse;
import com.ecommerce.webapp.dto.response.order.OrderResponse;
import com.ecommerce.webapp.dto.response.order.OrderResponseOrder;
import com.ecommerce.webapp.dto.response.order.OrdersResponseOrderItem;
import com.ecommerce.webapp.entity.*;
import com.ecommerce.webapp.repository.InventoryRepository;
import com.ecommerce.webapp.util.ShopConstants;
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
            newCart.setOrderStatus(OrderStatus.CART);
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
                    cart.setOrderDate(LocalDateTime.now());
                    cart.setUserAndAddOrder(user);
                    return shopOrderRepository.save(cart);
                }
            } catch (Exception e){
                throw new InvalidOrderStateException(ShopConstants.ERR_CODE_INTERNAL_SERVER_ERROR, e.getMessage());
            }

            return ShopOrder.builder().orderStatus(OrderStatus.CANCELLED).build();

        } else {
            throw new InvalidOrderStateException(ShopConstants.ERR_CODE_INTERNAL_SERVER_ERROR, "Invalid order request");
        }
    }

    public void removeItemXFromCart(UserEntity user, int itemId) {
        ShopOrder cart = getOrCreateCart(user);
        cart.getOrderItems().removeIf(item -> item.getId() == itemId);
        shopOrderRepository.save(cart);
    }

    public void removeItemsFromCart(UserEntity user, DeleteItemsRequest request) {
        if(request != null && request.getItemIds() != null) {
            ShopOrder cart = getOrCreateCart(user);

            for(Integer itemId : request.getItemIds()) {
                cart.getOrderItems().removeIf(item -> item.getId() == itemId);
            }
            shopOrderRepository.save(cart);

        }
     }


    public void updateItemQuantity(UserEntity user, int itemId, int newQuantity) {
        ShopOrder cart = getOrCreateCart(user);

        Integer productId = cart.getOrderItems().stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .map(OrderItem::getProductID) // Map to the productId if present
                .orElse(null); // Return null if Optional is empty

        if (productId != null ) {
            Product p = productRepository.findById(itemId);
            Optional<OrderItem> orderItem = cart.getOrderItems().stream()
                    .filter(item -> item.getId() == itemId)
                    .findFirst();

            if (p.getInventory() != null && orderItem.isPresent()) {
                int availableInventory = p.getInventory().stream().filter(inv ->
                    inv.getSize().equals(orderItem.get().getSize()) &&
                    inv.getColor().equals(orderItem.get().getColor())
                ).findFirst().map(Inventory::getQuantity).orElse(0);

                if (availableInventory >= newQuantity) {
                    orderItem.get().setQuantity(newQuantity);
                } else {
                    throw new InvalidOrderStateException(ShopConstants.ERR_CODE_INSUFFICIENT_INVENTORY, "Insufficient inventory");
                }
            }
         }

        shopOrderRepository.save(cart);
    }

    public void updateItemSize(UserEntity user, int itemId, String newSize){

        ShopOrder cart = getOrCreateCart(user);

        Integer productId = cart.getOrderItems().stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .map(OrderItem::getProductID) // Map to the productId if present
                .orElse(null); // Return null if Optional is empty

        if (productId != null ) {
            Optional<Product> p = productRepository.findById(productId);

            Optional<OrderItem> orderItem = cart.getOrderItems().stream()
                    .filter(item -> item.getId() == itemId)
                    .findFirst();

            if (p != null && p.isPresent() && p.get().getInventory() != null && orderItem.isPresent()) {
                int availableInventory = p.get().getInventory().stream().filter(inv ->
                        inv.getSize().equals(newSize) &&
                                inv.getColor().equals(orderItem.get().getColor())
                ).findFirst().map(Inventory::getQuantity).orElse(0);

                if (availableInventory > 0 && availableInventory > orderItem.get().quantity) {
                    orderItem.get().setSize(newSize);
                } else {
                    throw new InvalidOrderStateException(ShopConstants.ERR_CODE_INSUFFICIENT_INVENTORY, "Insufficient inventory");
                }
            } else {
                throw new InvalidOrderStateException(ShopConstants.ERR_CODE_INTERNAL_SERVER_ERROR, "No Product found");
            }
        }

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
                throw new InvalidOrderStateException(ShopConstants.ERR_CODE_INTERNAL_SERVER_ERROR, e.getMessage());
            }

            return ShopOrder.builder().orderStatus(OrderStatus.CANCELLED).build();

        } else {
            throw new InvalidOrderStateException(ShopConstants.ERR_CODE_INTERNAL_SERVER_ERROR, "Invalid order request");
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
                        throw new InvalidOrderStateException(ShopConstants.ERR_CODE_INSUFFICIENT_INVENTORY, "Inventory not found for product ID: " + item.getProductID());
                    } else if (inventory.getQuantity() < item.getQuantity()) {
                        throw new InvalidOrderStateException(ShopConstants.ERR_CODE_INSUFFICIENT_INVENTORY, "Insufficient inventory for product ID: " + item.getProductID());
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

                        String skuImage = product.getInventory().stream().filter(a -> a.getColor().equalsIgnoreCase(item.getColor())).findFirst().map(Inventory::getImage).orElse(null);

                        OrderItem orderItem = OrderItem.builder()
                                .orderID(cart.getId())
                                .productID(item.getId())
                                .color(item.getColor())
                                .quantity(item.getQuantity())
                                .size(item.getSize())
                                .image(skuImage)
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
                                .image(orderItem.getImage())
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
        throw new InvalidOrderStateException(ShopConstants.ERR_CODE_INTERNAL_SERVER_ERROR, "Cannot cancel order with status: " + order.getOrderStatus());
    }

    protected boolean sanitizeOrderRequest(SubmitOrderRequest request){

        if(request.getUsername() == null || request.getUsername().isEmpty() || request.getItems().isEmpty()){
            throw new InvalidOrderStateException(ShopConstants.ERR_CODE_BAD_REQUEST, "Invalid order request username or items should not be empty");
        }

        return true;

    }

}
