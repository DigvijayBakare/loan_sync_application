package com.loansync.userservice.controller;

import com.loansync.userservice.security.CustomUserDetailsService;
import com.loansync.userservice.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2/auth")
public class AuthController {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @GetMapping("/getAuthUser/{username}")
    public ResponseEntity<UserDetails> getUserDetails(@PathVariable String username) {
        UserDetails details = customUserDetailsService.loadUserByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(details);
    }
}
