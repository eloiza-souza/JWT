package com.eloiza.JWT.repositories;

import com.eloiza.JWT.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByName(String department);
}
