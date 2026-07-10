package com.loansync.userservice.service;

import com.loansync.userservice.dto.RegisterRequest;
import com.loansync.userservice.entity.Users;

import java.util.List;

public interface CustomUsersService {
    // Create user
    Users createUser(RegisterRequest request);

    // get all users
    List<Users> getAllUsers();

    // get user by username
    Users getUser(String username);

    // Update user
    Users updateUser(String username, RegisterRequest request);

    // Delete user
    void deleteUser(String username);
}