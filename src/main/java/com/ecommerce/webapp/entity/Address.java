package com.ecommerce.webapp.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "address_type")
    private String addressType;
    @Column(name = "address_line1")
    private String addressLine1;
    @Column(name = "address_line2")
    private String addressLine2;
    @Column(name = "city")
    private String city;
    @Column(name = "district")
    private String district;
    @Column(name = "state")
    private String state;
    @Column(name = "pincode")
    private int pincode;

    @Column(name = "country")
    private String country;



    public Address(String addressType, String addressLine1, String addressLine2, String city, String district, String state, int pincode, String country) {
        this.addressType = addressType;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.district = district;
        this.state = state;
        this.pincode = pincode;
        this.country = country;
    }

    public Address() {
    }
}
