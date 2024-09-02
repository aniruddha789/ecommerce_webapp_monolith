package com.ecommerce.webapp.service;

import com.ecommerce.webapp.repository.ShopOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    ShopOrderRepository shopOrderRepository;



}
