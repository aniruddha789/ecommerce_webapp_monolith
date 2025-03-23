package com.ecommerce.webapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CurrentTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@Table(name = "shop_order")
public class ShopOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "userid")
    private Integer userId;

    @Column(name = "order_date", nullable = false)
    @CurrentTimestamp
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonBackReference
    private UserEntity user;

    @OneToMany(mappedBy = "orderID", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    public ShopOrder(UserEntity user, LocalDateTime orderDate) {
        this.userId = user.getId();
        this.orderDate = orderDate;
        this.orderStatus = OrderStatus.CART;
        this.orderItems = new ArrayList<>();
    }

    public ShopOrder() {
    }

    // Add a method to set the user and update the relationship
    public void setUserAndAddOrder(UserEntity user) {
        this.userId = user.getId();
        this.user = user;
        user.getOrders().add(this);
    }
}

