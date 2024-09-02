package com.ecommerce.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "orderID")
    public int orderID;

    @Column(name = "productID")
    public int productID;

    @Column(name = "quantity")
    public int quantity;

    @Column(name = "size")
    public String size;

    @ManyToOne
    @JoinColumn(name = "orderID", nullable = false, updatable = false, insertable = false)
    private ShopOrder order;

    @ManyToOne
    @JoinColumn(name = "productID", nullable = false, updatable = false, insertable = false)
    private Product product;

    public OrderItem(int orderID, int productID, int quantity, String size) {
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.size = size;
    }

    public OrderItem() {
    }
}
