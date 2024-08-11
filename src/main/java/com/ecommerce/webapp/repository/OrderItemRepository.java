package com.ecommerce.webapp.repository;

import com.ecommerce.webapp.entity.OrderItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Integer> {

    ArrayList<OrderItem> findAll();

    ArrayList<OrderItem> findByOrderId(int orderId);

    OrderItem findById(int id);

    OrderItem save(OrderItem orderItem);

    void deleteById(int id);

    void deleteAllByOrderId(int orderId);




}
