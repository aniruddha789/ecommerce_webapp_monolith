package com.ecommerce.webapp.controller;


import com.ecommerce.webapp.dto.request.GoogleAuthRequest;
import com.ecommerce.webapp.dto.request.LoginDTO;
import com.ecommerce.webapp.dto.request.RegisterDTO;
import com.ecommerce.webapp.dto.response.LoginResponse;
import com.ecommerce.webapp.dto.response.Status;
import com.ecommerce.webapp.entity.Address;
import com.ecommerce.webapp.entity.Role;
import com.ecommerce.webapp.entity.UserEntity;
import com.ecommerce.webapp.repository.UserEntityRepository;
import com.ecommerce.webapp.service.KeyManagementService;
import com.ecommerce.webapp.service.UserService;
import com.ecommerce.webapp.util.JWTUtil;
import com.ecommerce.webapp.util.PasswordGenerator;
import org.apache.commons.lang3.BooleanUtils;
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
import java.util.Optional;
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

    @Autowired
    UserEntityRepository userRepository;



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

    @PostMapping("/updatePassword")
    public Status updatePassword(@RequestBody LoginDTO loginDTO) {
        String decryptedPassword = keyManagementService.decryptPassword(loginDTO.getPassword());
        loginDTO.setPassword(decryptedPassword);

        Status status = userService.updatePassword(loginDTO);
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
        String username = userDetails.getUsername();
        UserEntity userEntity = userService.findByUsername(username);
        String firstname = userEntity.getFirstname();

        /** Handle admin login */
        if(BooleanUtils.isTrue(loginDTO.getAdminLogin())) {
            if (userEntity.getRoles().stream().filter(a -> a.getName().equalsIgnoreCase("ADMIN")).count() == 0) {
                return ResponseEntity.badRequest().body(new LoginResponse(null, "FAIL", "Admin login failed", null, null));
            }
        }

        final String token = jwtUtil.generateToken(userDetails.getUsername()).trim(); // Ensure no whitespace

        return ResponseEntity.ok(new LoginResponse(token, "SUCCESS", "Token generated successfully", 
        username, firstname));
    }

    @PostMapping("/google-login")
    public ResponseEntity<LoginResponse> googleLogin(@RequestBody GoogleAuthRequest googleAuthRequest) {
        try {
            // 1. Validate Firebase token (optional but recommended)
            // If you want to verify the token with Firebase, you can add Firebase Admin SDK

            // 2. Check if user exists by firebaseUid
            Optional<UserEntity> existingUser = userRepository.findByFirebaseUid(googleAuthRequest.getFirebaseUid());
            UserEntity user;

            if (existingUser.isPresent()) {
                // User exists - use existing user
                user = existingUser.get();
            } else {
                // User doesn't exist - create new user

                String firebaseUid = googleAuthRequest.getFirebaseUid();
                String email = googleAuthRequest.getEmail();

                // Generate a unique username (could be from email or display name)
                String baseUsername = googleAuthRequest.getEmail().split("@")[0];
                String username = generateUniqueUsername(baseUsername);

                // Set names from display name
                String[] nameParts = googleAuthRequest.getDisplayName().split(" ", 2);
                String firstName = nameParts[0];
                String lastName = nameParts.length > 1 ? nameParts[1] : "";

                RegisterDTO registerDTO = RegisterDTO.builder()
                        .username(username)
                        .firstname(firstName)
                        .lastname(lastName)
                        .firebaseUid(firebaseUid)
                        .email(email)
                        .password(PasswordGenerator.generateRandomPassword(10))
                        .build();

                Status status = userService.register(registerDTO);
                if (!status.getStatus().equals("SUCCESS")) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new LoginResponse(null, "FAIL", "Google authentication failed: " + status.getMessage(), null, null));
                }

                user = userRepository.findByUsername(username);
            }

            if(user != null) {
                // 3. Generate JWT token (same as password login)
                final String token = jwtUtil.generateToken(user.getUsername()).trim();

                // 4. Return the same response structure as password login
                return ResponseEntity.ok(
                        new LoginResponse(token, "SUCCESS", "Google authentication successful",
                                user.getUsername(), user.getFirstname()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(null, "FAIL", "Google authentication failed: User not found", null, null));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, "FAIL", "Google authentication failed: " + e.getMessage(), null, null));
        }
    }

    /** Helper method to generate unique username */
    private String generateUniqueUsername(String baseUsername) {
        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter++;
        }

        return username;
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
