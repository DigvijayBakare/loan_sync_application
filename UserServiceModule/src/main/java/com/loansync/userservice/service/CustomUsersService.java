package com.loansync.userservice.service;

import com.loansync.userservice.dto.RegisterRequest;
import com.loansync.userservice.entity.Users;

import java.util.List;

public interface CustomUsersService {
    // Create user
    RegisterRequest createUser(RegisterRequest request);

    // get all users
    List<Users> findAll();

    // get user by username
    Users getUser(String username);

    // Update user
    Users updateUser(String username, RegisterRequest request);

    // Delete user
    void deleteUser(String username);
}