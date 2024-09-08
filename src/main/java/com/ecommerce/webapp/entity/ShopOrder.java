package com.ecommerce.webapp.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Data
@Table(name = "shop_order")
public class ShopOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @Column(name = "userid")
//    private String userID;

    @Column(name = "order_date", nullable = false)
    @CurrentTimestamp
    private LocalDateTime orderDate;


    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name =  "userid", updatable = false, insertable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "orderID", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;



    public ShopOrder(UserEntity user, LocalDateTime orderDate) {
        this.user = user;
        this.orderDate = orderDate;
        this.orderStatus = OrderStatus.CART;
    }

    public ShopOrder() {
    }



}

