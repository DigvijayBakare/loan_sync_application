package com.loansync.userservice.repository;

import com.loansync.userservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Role.Name name);

    boolean existsByName(Role.Name name);
}
