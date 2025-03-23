package com.ecommerce.webapp.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;


    @Column(name = "phone")
    private String phone;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "userid", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "roleID", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_address", joinColumns = @JoinColumn(name = "userid", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "addressid", referencedColumnName = "id"))
    private Set<Address> addresses = new HashSet<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ShopOrder> orders = new ArrayList<>();


    public UserEntity(String firstname, String lastname, String username, String password, String email, String phone) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

    public void addAddress(Address address) {
        addresses.add(address);
    }

    public UserEntity() {
    }

    // Method to add order and update the bidirectional relationship
    public void addOrder(ShopOrder order) {
        orders.add(order);
        order.setUser(this);
    }
}
