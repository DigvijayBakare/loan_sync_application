package com.loansync.userservice.service.impl;

import com.loansync.userservice.dto.AuthResponse;
import com.loansync.userservice.dto.LoginRequest;
import com.loansync.userservice.dto.RegisterRequest;
import com.loansync.userservice.entity.Role;
import com.loansync.userservice.entity.Users;
import com.loansync.userservice.exception.EmailAlreadyExistsException;
import com.loansync.userservice.exception.InvalidCredentialsException;
import com.loansync.userservice.exception.UserNotFoundException;
import com.loansync.userservice.repository.RolesRepository;
import com.loansync.userservice.repository.UserRepository;
import com.loansync.userservice.security.JwtService;
import com.loansync.userservice.security.UserPrincipal;
import com.loansync.userservice.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager manager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        boolean existsByEmail = userRepository.existsByEmail(request.getEmail());

        if (existsByEmail) throw new EmailAlreadyExistsException("Username with email: " + request.getEmail() + " already present in the DB");

        Set<Role> roles = resolveRoles(request.getRole());

        Users user = Users.builder()
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .enabled(true)
                .verified(false)
                .roles(roles)
                .build();

        userRepository.save(user);
        UserPrincipal userPrincipal = new UserPrincipal(user);
        return buildAuthResponse(userPrincipal);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            manager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException();
        }

        Users user = userRepository.findByEmail(request.getEmail()).orElseThrow(InvalidCredentialsException::new);
        UserPrincipal userPrincipal = new UserPrincipal(user);
        return buildAuthResponse(userPrincipal);
    }

    private AuthResponse buildAuthResponse(UserPrincipal user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresInMs(jwtService.getAccessTokenExpirationMs())
                .build();
    }

    private Set<Role> resolveRoles(Set<String> requestedRole) {
        if (requestedRole == null || requestedRole.isEmpty())
            requestedRole = Set.of(Role.Name.BORROWER.name());

        Set<Role.Name> roleEnum;

        try {
            roleEnum = requestedRole.stream().map(Role.Name::valueOf).collect(Collectors.toSet());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown role: " + requestedRole);
        }

        return roleEnum.stream().map(role -> rolesRepository.findByName(role)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Role not found: " + roleEnum)))
                .collect(Collectors.toSet());
    }
}
