package com.eloiza.JWT.services;

import com.eloiza.JWT.controllers.dtos.RegisterUserDto;
import com.eloiza.JWT.models.Department;
import com.eloiza.JWT.models.Role;
import com.eloiza.JWT.models.User;
import com.eloiza.JWT.repositories.DepartmentRepository;
import com.eloiza.JWT.repositories.RoleRepository;
import com.eloiza.JWT.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void registerUser(RegisterUserDto registerUserDto) {
        if (userRepository.existsByUsername(registerUserDto.getUsername())) {
            throw new RuntimeException("Usuário já cadastrado no sistema");
        }

        User user = new User();
        user.setUsername(registerUserDto.getUsername());
        user.setEmail(registerUserDto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(registerUserDto.getPassword()));
        user.setName(registerUserDto.getName());

        // Verificar se os papéis já existem no banco
        Set<Role> roles = registerUserDto.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseGet(() -> roleRepository.save(new Role(roleName))))
                .collect(Collectors.toSet());

        // Verificar se o departamento já existe no banco
        Department department = departmentRepository.findByName(registerUserDto.getDepartment())
                .orElseGet(() -> departmentRepository.save(new Department(registerUserDto.getDepartment())));

        user.setRoles(roles);
        user.setDepartment(department);
        userRepository.save(user);
    }
}