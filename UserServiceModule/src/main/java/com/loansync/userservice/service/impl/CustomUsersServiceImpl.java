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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
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

        Set<Role> roles = resolveRoles(request.getRole());

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
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    @Override
    public Users updateUser (String username, RegisterRequest request) {
        Users user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        Set<Role> roles = resolveRoles(request.getRole());

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(request.getPassword());
        user.setRoles(roles);

        userRepository.save(user);

        return user;
    }

    @Override
    public void deleteUser(String username) {
        boolean existByEmail = userRepository.existsByEmail(username);

        userRepository.deleteByEmail(username);
    }

    private Set<Role> resolveRoles(Set<String> requestedRoles) {
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            requestedRoles = Set.of(Role.Name.BORROWER.name());
        }

        Set<Role.Name> roleEnum;

        try {
            roleEnum = requestedRoles.stream().map(Role.Name::valueOf).collect(Collectors.toSet());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown role: " + requestedRoles);
        }

        return roleEnum.stream()
                .map(role -> rolesRepository.findByName(role)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Role not found: " + roleEnum)))
                .collect(Collectors.toSet());
    }
}
