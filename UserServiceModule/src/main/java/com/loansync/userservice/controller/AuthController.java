package com.loansync.userservice.controller;

import com.loansync.userservice.dto.AuthResponse;
import com.loansync.userservice.dto.LoginRequest;
import com.loansync.userservice.dto.RegisterRequest;
import com.loansync.userservice.security.CustomUserDetailsService;
import com.loansync.userservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v2/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private final AuthService authService;

    @GetMapping("/getAuthUser/{username}")
    public ResponseEntity<UserDetails> getUserDetails(@PathVariable String username) {
        UserDetails details = customUserDetailsService.loadUserByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(details);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login (@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
