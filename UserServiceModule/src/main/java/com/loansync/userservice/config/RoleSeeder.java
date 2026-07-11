package com.loansync.userservice.config;

import com.loansync.userservice.entity.Role;
import com.loansync.userservice.repository.RolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final RolesRepository roleRepository;

    @Override
    public void run(String... args) {

        for(Role.Name roleName : Role.Name.values()) {

            if(!roleRepository.existsByName(roleName)) {

                Role role = Role.builder()
                        .name(roleName)
                        .description(roleName + " role")
                        .build();

                roleRepository.save(role);
            }
        }
    }
}
