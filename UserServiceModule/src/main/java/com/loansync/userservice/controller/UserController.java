package com.loansync.userservice.controller;

import com.loansync.userservice.dto.RegisterRequest;
import com.loansync.userservice.entity.Users;
import com.loansync.userservice.service.CustomUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/users")
public class UserController {
    private CustomUsersService usersService;

    @Autowired
    public UserController(CustomUsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping
    public ResponseEntity<RegisterRequest> createUsers(@RequestBody RegisterRequest request) {
        RegisterRequest registeredUser = usersService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> usersList = usersService.findAll();
        return ResponseEntity.status(HttpStatus.FOUND).body(usersList);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Users> getUsersDetail(@PathVariable String username) {
        Users user = usersService.getUser(username);
        return ResponseEntity.status(HttpStatus.FOUND).body(user);
    }

    @PutMapping("/{username}")
    public ResponseEntity<Users> updateUsersDetails (@PathVariable String username, @RequestBody RegisterRequest request) {
        Users updatedUser = usersService.updateUser(username, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Object> deleteUser(@PathVariable String username) {
        usersService.deleteUser(username);
        return ResponseEntity.status(HttpStatus.OK).body("User with username: " + username + " deleted successfully");
    }
}
