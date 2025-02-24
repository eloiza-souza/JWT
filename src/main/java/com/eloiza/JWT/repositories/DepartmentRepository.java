package com.eloiza.JWT.repositories;

import com.eloiza.JWT.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
