package com.eloiza.JWT.services;

import com.eloiza.JWT.controllers.dtos.AuthUserDto;
import com.eloiza.JWT.controllers.dtos.Departments;
import com.eloiza.JWT.controllers.dtos.RegisterUserDto;
import com.eloiza.JWT.infra.jwt.JwtTokenProvider;
import com.eloiza.JWT.models.CustomUserDetails;
import com.eloiza.JWT.models.Department;
import com.eloiza.JWT.models.Role;
import com.eloiza.JWT.models.User;
import com.eloiza.JWT.repositories.DepartmentRepository;
import com.eloiza.JWT.repositories.RoleRepository;
import com.eloiza.JWT.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final DepartmentRepository departmentRepository;

    @Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private final JwtTokenProvider jwtTokenProvider;

    public void registerUser(RegisterUserDto registerUserDto) {
        if (userRepository.existsByUsername(registerUserDto.getUsername())) {
            throw new RuntimeException("Usuário já cadastrado no sistema");
        }

        User user = new User();
        user.setUsername(registerUserDto.getUsername());
        user.setEmail(registerUserDto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(registerUserDto.getPassword()));
        user.setName(registerUserDto.getName());

        Set<Role> roles = registerUserDto.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseGet(() -> roleRepository.save(new Role(roleName))))
                .collect(Collectors.toSet());

        Department department = departmentRepository.findByName(registerUserDto.getDepartment())
                .orElseGet(() -> departmentRepository.save(new Department(registerUserDto.getDepartment())));

        user.setRoles(roles);
        user.setDepartment(department);
        userRepository.save(user);
    }

    public AuthUserDto getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            String username = userDetails.getUsername();
            Departments department = userDetails.getDepartment();

            // Retorna as informações do usuário autenticado
            return new AuthUserDto("Bem-vindo, " + username + "!", department.name());
        }

        throw new IllegalArgumentException("Não foi possível extrair informações do usuário autenticado");
    }
}