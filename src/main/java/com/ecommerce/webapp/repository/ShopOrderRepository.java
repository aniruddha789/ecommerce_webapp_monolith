package com.ecommerce.webapp.repository;


import com.ecommerce.webapp.entity.OrderStatus;
import com.ecommerce.webapp.entity.ShopOrder;
import com.ecommerce.webapp.entity.UserEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShopOrderRepository extends CrudRepository<ShopOrder, Integer> {
    
    ArrayList<ShopOrder> findAll();

    Optional<ShopOrder> findById(int id);

    List<ShopOrder> findByUserId(Integer userID);

    ShopOrder save(ShopOrder order);

    void deleteById(int id);

    Page<ShopOrder> findAll(Pageable pageable);

    Page<ShopOrder> findByUserId(Long userId, Pageable pageable);

    Optional<ShopOrder> findByUserAndOrderStatus(UserEntity user, OrderStatus status);

    List<ShopOrder> findByUserAndOrderStatusNot(UserEntity user, OrderStatus status);
}
