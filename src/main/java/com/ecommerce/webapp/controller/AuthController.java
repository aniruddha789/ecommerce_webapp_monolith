package com.ecommerce.webapp.controller;

import com.ecommerce.webapp.service.KeyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    KeyManagementService keyManagementService;

    @GetMapping("/public-key")
    public ResponseEntity<Map<String, String>> getPublicKey() {
        return ResponseEntity.ok(Map.of("publicKey", keyManagementService.getPublicKeyInPEM()));
    }


}
