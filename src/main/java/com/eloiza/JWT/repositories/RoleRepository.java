package com.eloiza.JWT.repositories;

import com.eloiza.JWT.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
}
