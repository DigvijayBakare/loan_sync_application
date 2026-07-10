package com.loansync.userservice.service.impl;

import com.loansync.userservice.dto.RegisterRequest;
import com.loansync.userservice.entity.Role;
import com.loansync.userservice.entity.Users;
import com.loansync.userservice.exception.EmailAlreadyExistsException;
import com.loansync.userservice.exception.UserNotFoundException;
import com.loansync.userservice.repository.RolesRepository;
import com.loansync.userservice.repository.UserRepository;
import com.loansync.userservice.service.CustomUsersService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomUsersServiceImpl implements CustomUsersService {
    private final UserRepository userRepository;
    private final RegisterRequest request;
    private final RolesRepository rolesRepository;

    public CustomUsersServiceImpl(UserRepository userRepository, RegisterRequest request, RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.request = request;
        this.rolesRepository = rolesRepository;
    }

    // Create a user
    @Override
    public Users createUser(RegisterRequest request) {
        boolean existByEmail = userRepository.existByEmail(request.getEmail());

        if (existByEmail) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        String roleName = (request.getRole() != null && !request.getRole().isBlank())
                ? request.getRole().toUpperCase() : Role.Name.BORROWER.name();

        Role role = rolesRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + roleName));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        Users user = Users.builder()
                .email(request.getEmail()).password(request.getPassword()).firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .enabled(true)
                .verified(false)
                .roles(roles)
                .build();

        userRepository.save(user);

        return user;
    }

    @Override
    public List<Users> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public Users getUser (String username) {
        return userRepository.findByEmail(username).get();
    }

    @Override
    public Users updateUser (String username, RegisterRequest request) {
        boolean existByEmail = userRepository.existByEmail(username);
        if (!existByEmail) throw new UserNotFoundException("User with username: " + username + "not available!");

        String roleName = (request.getRole() != null && !request.getRole().isBlank())
                ? request.getRole().toUpperCase() : Role.Name.BORROWER.name();

        Role role = rolesRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + roleName));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        Users user = Users.builder()
                .email(request.getEmail()).password(request.getPassword()).firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .enabled(true)
                .verified(false)
                .roles(roles)
                .build();

        userRepository.save(user);

        return user;
    }

    @Override
    public void deleteUser(String username) {
        boolean existByEmail = userRepository.existByEmail(username);

        userRepository.deleteByEmail(username);
    }
}
