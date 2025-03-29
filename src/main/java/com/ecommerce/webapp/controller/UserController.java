package com.ecommerce.webapp.controller;


import com.ecommerce.webapp.dto.request.LoginDTO;
import com.ecommerce.webapp.dto.request.RegisterDTO;
import com.ecommerce.webapp.dto.response.LoginResponse;
import com.ecommerce.webapp.dto.response.Status;
import com.ecommerce.webapp.entity.Address;
import com.ecommerce.webapp.entity.Role;
import com.ecommerce.webapp.entity.UserEntity;
import com.ecommerce.webapp.service.KeyManagementService;
import com.ecommerce.webapp.service.UserService;
import com.ecommerce.webapp.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
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

    @Autowired
    KeyManagementService keyManagementService;



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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginDTO loginDTO) {
        try {
            String decryptedPassword = keyManagementService.decryptPassword(loginDTO.getPassword());
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), decryptedPassword)
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus .UNAUTHORIZED)
                .body(new LoginResponse(null, "FAIL", "Invalid username or password", null, null));
        }

        final UserDetails userDetails = userService.loadUserByUsername(loginDTO.getUsername());
        final String token = jwtUtil.generateToken(userDetails.getUsername()).trim(); // Ensure no whitespace
        String username = userDetails.getUsername();
        String firstname = userService.findByUsername(username).getFirstname();
        return ResponseEntity.ok(new LoginResponse(token, "SUCCESS", "Token generated successfully", 
        username, firstname));
    }


    @GetMapping("/getUser")
    public ArrayList<UserEntity> getUser(){

        return userService.getAllUsers();

    }

    @PostMapping("/addAddress/{username}")
    public Status addAddressToUser(@PathVariable String username, @RequestBody Address address) {
        Status status = userService.addAddressToUser(username, address);
        return status;
    }

    @GetMapping("/getAddress/{username}")
    public Set<Address> getAddress(@PathVariable String username){
        return userService.getAddress(username);
    }

    @GetMapping("/getRoles/{uname}")
    public ArrayList<Role> getRoles(@PathVariable String uname){
        return userService.getRoles(uname);
    }

    @GetMapping("/isValidToken")
    public boolean isValidToken(@RequestParam String token){
        return jwtUtil.isValidToken(token);
    }

}
