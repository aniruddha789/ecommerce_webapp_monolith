package com.ecommerce.webapp.dto.request;

import com.ecommerce.webapp.entity.Role;

import java.util.ArrayList;

public class RegisterDTO {

    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;

    private String phone;

    private ArrayList<Role> roles;

    public RegisterDTO() {
    }

    public RegisterDTO(String firstname, String lastname, String email, String username, String password, String phone, ArrayList<Role> roles) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.roles = roles;
    }

    public ArrayList<Role> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<Role> roles) {
        this.roles = roles;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
