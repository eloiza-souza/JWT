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

@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void registerUser(RegisterUserDto registerUserDto){
        if (userRepository.existsByUsername(registerUserDto.getUsername())){
            throw new RuntimeException("Usuário já cadastrado no sistema");
        }

        User user = new User();
        user.setUsername(registerUserDto.getUsername());
        user.setEmail(registerUserDto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(registerUserDto.getPassword()));
        user.setName(registerUserDto.getName());

        Set<Role> roles = registerUserDto.getRoles().stream().map(r -> new Role(r.name())).collect(Collectors.toSet());
        roleRepository.saveAll(roles);

        Department department = registerUserDto.getDepartment();
        departmentRepository.save(department);

        user.setRoles(roles);
        userRepository.save(user);

    }
}
