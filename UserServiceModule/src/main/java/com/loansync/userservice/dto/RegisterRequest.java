package com.loansync.userservice.dto;

import com.loansync.userservice.entity.Role;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Data
public class RegisterRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String phoneNumber;

    // SUPER_ADMIN, ADMIN, LENDER or BORROWER - defaults to BORROWER if omitted in the service layer
    private Set<String> role;
}

