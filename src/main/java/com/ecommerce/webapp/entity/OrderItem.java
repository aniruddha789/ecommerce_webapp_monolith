package com.ecommerce.webapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_item")
@JsonIgnoreProperties("order")
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

    @Column(name = "color")
    public String color;

    @Column(name = "image")
    public String image;

    @ManyToOne
    @JoinColumn(name = "orderID", nullable = false, updatable = false, insertable = false)
    private ShopOrder order;

    @ManyToOne
    @JoinColumn(name = "productID", nullable = false, updatable = false, insertable = false)
    private Product product;

    public OrderItem(int orderID, int productID, int quantity, String size, String color) {
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.size = size;
        this.color = color;
    }

}
