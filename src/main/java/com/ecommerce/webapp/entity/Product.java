package com.ecommerce.webapp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @Column(name = "type")
    private String type;
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
    @JsonManagedReference
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventory> inventory;


    public Product(String name, String type, String brandid, String color, int listprice, String description, int supplierID, String image) {
        this.name = name;
        this.type = type;
        this.brandid = brandid;
        this.color = color;
        this.listprice = listprice;
        this.description = description;
        this.supplierID = supplierID;
        this.image = image;
    }

    public Product() {
    }

    @Override
    public boolean equals(Object obj) {

        //check if object is instance of Product class
        if(! obj.getClass().isInstance(Product.class) ) return false;

        return this.equals(obj);
    }

}