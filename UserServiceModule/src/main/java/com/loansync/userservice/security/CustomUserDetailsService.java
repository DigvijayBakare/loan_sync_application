package com.loansync.userservice.security;

import com.loansync.userservice.entity.Users;
import com.loansync.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.debug("Username in CustomUserDetailsService: {}", username);
        Optional<Users> user = userRepository.findByEmail(username);
        return user.map(UserPrincipal::new).orElseThrow(()->new UsernameNotFoundException("Invalid Username"));
    }
}
