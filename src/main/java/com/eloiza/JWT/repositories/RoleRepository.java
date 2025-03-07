package com.eloiza.JWT.repositories;

import com.eloiza.JWT.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String roleName);
}
