package com.loansync.userservice.repository;

import com.loansync.userservice.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {
    // custom Spring Data JPA derived query method

    Optional<Users> findByEmail (String email);

    boolean existsByEmail(String email);

    List<Users> findAll();

    void deleteByEmail(String email);
}
