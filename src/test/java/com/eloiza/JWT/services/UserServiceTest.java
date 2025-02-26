package com.eloiza.JWT.services;

import com.eloiza.JWT.controllers.dtos.AuthUserDto;
import com.eloiza.JWT.controllers.dtos.RegisterUserDto;
import com.eloiza.JWT.infra.jwt.JwtTokenProvider;
import com.eloiza.JWT.models.CustomUserDetails;
import com.eloiza.JWT.models.Department;
import com.eloiza.JWT.models.Role;
import com.eloiza.JWT.models.User;
import com.eloiza.JWT.repositories.DepartmentRepository;
import com.eloiza.JWT.repositories.RoleRepository;
import com.eloiza.JWT.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void registerUser_Success() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setName("Test User");
        registerUserDto.setUsername("testuser");
        registerUserDto.setEmail("testuser@example.com");
        registerUserDto.setPassword("password");
        registerUserDto.setRoles(Set.of("ROLE_USER"));
        registerUserDto.setDepartment("IT");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(new Role("ROLE_USER"));
        when(departmentRepository.findByName("IT")).thenReturn(Optional.empty());
        when(departmentRepository.save(any(Department.class))).thenReturn(new Department("IT"));

        userService.registerUser(registerUserDto);

        verify(userRepository).save(any(User.class));
    }

    @Test
    public void registerUser_ExistingUser() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername("testuser");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.registerUser(registerUserDto));
        assertEquals("Usuário já cadastrado no sistema", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    public void getAuthenticatedUser_Success() {
        User user = new User();
        user.setName("Test User");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRoles(Set.of());
        user.setDepartment(new Department("IT"));

        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContextHolder.setContext(securityContext);

        AuthUserDto authUserDto = userService.getAuthenticatedUser();

        assertNotNull(authUserDto);
        assertEquals("Bem-vindo, Test User!", authUserDto.getMessage());
        assertEquals("IT", authUserDto.getDepartment());
    }

    @Test
    public void getAuthenticatedUser_NotAuthenticatedUser() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContextHolder.setContext(securityContext);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.getAuthenticatedUser());
        assertEquals("Usuário não autenticado", exception.getMessage());

    }

    @Test
    public void getAuthenticatedUser_NullAuthentication() {
        when(securityContext.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.getAuthenticatedUser());
        assertEquals("Usuário não autenticado", exception.getMessage());
    }

    @Test
    public void getAuthenticatedUser_principalIsNotCustomUserDetails() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.getAuthenticatedUser());
        assertEquals("Não foi possível extrair informações do usuário autenticado", exception.getMessage());
    }


}
