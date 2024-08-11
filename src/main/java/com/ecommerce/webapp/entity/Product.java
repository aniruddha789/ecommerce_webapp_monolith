package com.ecommerce.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "brandid")
    private String brandid;
    @Column(name = "color")
    private String color;
    @Column(name = "listprice")
    private int listprice;
    @Column(name = "description")
    private String description;
    @Column(name = "supplierID")
    private int supplierID;
    @Column(name = "image")
    private String image;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventory> inventory;



    public Product(String name, String brandid, String color, int listprice, String description, int supplierID, String image) {
        this.name = name;
        this.brandid = brandid;
        this.color = color;
        this.listprice = listprice;
        this.description = description;
        this.supplierID = supplierID;
        this.image = image;
    }

    public Product() {
    }

}