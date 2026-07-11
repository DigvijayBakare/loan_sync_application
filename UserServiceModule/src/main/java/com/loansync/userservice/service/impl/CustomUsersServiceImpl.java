package com.loansync.userservice.service.impl;

import com.loansync.userservice.dto.RegisterRequest;
import com.loansync.userservice.entity.Role;
import com.loansync.userservice.entity.Users;
import com.loansync.userservice.exception.EmailAlreadyExistsException;
import com.loansync.userservice.exception.UserNotFoundException;
import com.loansync.userservice.repository.RolesRepository;
import com.loansync.userservice.repository.UserRepository;
import com.loansync.userservice.service.CustomUsersService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUsersServiceImpl implements CustomUsersService {
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;

    public CustomUsersServiceImpl(UserRepository userRepository, RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
    }

    // Create a user
    @Override
    public RegisterRequest createUser(RegisterRequest request) {
        boolean existsByEmail = userRepository.existsByEmail(request.getEmail());

        if (existsByEmail) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Set<String> roleName = (request.getRole() != null && !request.getRole().isEmpty())
                ? request.getRole().stream().map(String::toUpperCase).collect(Collectors.toSet()) : Set.of(Role.Name.BORROWER.name());

        Set<Role.Name> roleEnum;

        try {
            roleEnum = roleName.stream().map(Role.Name::valueOf).collect(Collectors.toSet());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown role: " + roleName);
        }

        Set<Role> roles = roleEnum.stream()
                .map(role -> rolesRepository.findByName(role)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Role not found: " + roleEnum)))
                .collect(Collectors.toSet());

        Users user = Users.builder()
                .email(request.getEmail()).password(request.getPassword()).firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .enabled(true)
                .verified(false)
                .roles(roles)
                .build();

        userRepository.save(user);
        return request;
    }

    @Override
    public List<Users> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Users getUser (String username) {
        return userRepository.findByEmail(username).get();
    }

    @Override
    public Users updateUser (String username, RegisterRequest request) {
        boolean existByEmail = userRepository.existsByEmail(username);
        if (!existByEmail) throw new UserNotFoundException("User with username: " + username + "not available!");

        Set<String> roleName = (request.getRole() != null && !request.getRole().isEmpty())
                ? request.getRole().stream().map(String::toUpperCase).collect(Collectors.toSet()) : Set.of(Role.Name.BORROWER.name());

        Set<Role.Name> roleEnum;

        try {
            roleEnum = roleName.stream().map(Role.Name::valueOf).collect(Collectors.toSet());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown role: " + roleName);
        }

        Set<Role> roles = roleEnum.stream()
                .map(role -> rolesRepository.findByName(role)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Role not found: " + roleEnum)))
                .collect(Collectors.toSet());

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
        boolean existByEmail = userRepository.existsByEmail(username);

        userRepository.deleteByEmail(username);
    }
}
