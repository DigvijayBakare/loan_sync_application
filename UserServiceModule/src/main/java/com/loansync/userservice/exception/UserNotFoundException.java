package com.loansync.userservice.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super();
    }
    public UserNotFoundException(String username) {
        super("User with username: " + username + " does not found!");
    }
}
