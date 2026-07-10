package com.loansync.userservice.repository;

import com.loansync.userservice.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByEmail (String email);

    boolean existByEmail(String email);

    List<Users> getAllUsers();

    void deleteByEmail(String email);
}
