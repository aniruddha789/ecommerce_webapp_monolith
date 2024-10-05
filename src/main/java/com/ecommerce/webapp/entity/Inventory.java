package com.ecommerce.webapp.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "inventory")
public class Inventory {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "productID", nullable = false)
    private Product product;

    @Column(name = "size", nullable = false)
    private String size;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "image", nullable = false)
    private String image;


    public Inventory(Product product, String size, String color, int quantity, String image) {
        this.product = product;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.image = image;
    }

}
