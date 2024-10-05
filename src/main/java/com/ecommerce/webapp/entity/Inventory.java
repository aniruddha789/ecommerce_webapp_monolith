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

    @Column(name = "quantity", nullable = false)
    private int quantity;


    public Inventory(Product product, String size, int quantity) {
        this.product = product;
        this.size = size;
        this.quantity = quantity;
    }

}
