package com.ecommerce.webapp.controller;


import com.ecommerce.webapp.dto.request.LoginDTO;
import com.ecommerce.webapp.dto.request.RegisterDTO;
import com.ecommerce.webapp.dto.response.LoginResponse;
import com.ecommerce.webapp.dto.response.Status;
import com.ecommerce.webapp.entity.Address;
import com.ecommerce.webapp.entity.Role;
import com.ecommerce.webapp.entity.UserEntity;
import com.ecommerce.webapp.service.UserService;
import com.ecommerce.webapp.util.JWTUtil;
import com.ecommerce.webapp.util.StatusBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWTUtil jwtUtil;


    @PostMapping("/register")
    public Status register(@RequestBody RegisterDTO registerDTO){
        Status status = userService.register(registerDTO);

        return status;
    }

    @PostMapping("/delete")
    public Status deleteUser(@RequestBody LoginDTO loginDTO){
        Status status = userService.deleteUser(loginDTO);
        return status;
    }

    @GetMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginDTO loginDTO){

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword()));

        String token = jwtUtil.generateToken(loginDTO.getUsername());

        LoginResponse resp = new LoginResponse(token, "PASS", "Token generated successfully");

        return ResponseEntity.ok(resp);
    }


    @GetMapping("/getUser")
    public ArrayList<UserEntity> getUser(){

        return userService.getAllUsers();

    }

    @GetMapping("/getAddress")
    public Set<Address> getAddress(String username){
        return userService.getAddress(username);
    }

    @GetMapping("/getRoles/{uname}")
    public ArrayList<Role> getRoles(@PathVariable String uname){
        return userService.getRoles(uname);
    }



}
