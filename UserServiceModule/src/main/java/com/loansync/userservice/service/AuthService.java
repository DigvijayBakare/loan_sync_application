package com.loansync.userservice.service;

import com.loansync.userservice.dto.AuthResponse;
import com.loansync.userservice.dto.LoginRequest;
import com.loansync.userservice.dto.RegisterRequest;

public interface AuthService {
    public AuthResponse register(RegisterRequest request);
    public AuthResponse login (LoginRequest request);
}
