package com.eloiza.JWT.services;

import com.eloiza.JWT.controllers.dtos.AuthUserDto;
import com.eloiza.JWT.controllers.dtos.RegisterUserDto;
import com.eloiza.JWT.models.CustomUserDetails;
import com.eloiza.JWT.models.Department;
import com.eloiza.JWT.models.Role;
import com.eloiza.JWT.models.User;
import com.eloiza.JWT.repositories.DepartmentRepository;
import com.eloiza.JWT.repositories.RoleRepository;
import com.eloiza.JWT.repositories.UserRepository;
import com.eloiza.JWT.util.TestDataFactory;
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

import static com.eloiza.JWT.util.TestDataFactory.TEST_USER;
import static com.eloiza.JWT.util.TestDataFactory.createDefaultRegisterUserDto;
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
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void registerUser_Success() {
        RegisterUserDto registerUserDto = createDefaultRegisterUserDto();

        when(userRepository.existsByUsername(TEST_USER)).thenReturn(false);
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
        registerUserDto.setUsername(TEST_USER);

        when(userRepository.existsByUsername(TEST_USER)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.registerUser(registerUserDto));
        assertEquals("Usuário já cadastrado no sistema", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    public void getAuthenticatedUser_Success() {

        User user = TestDataFactory.createDefaultUser();

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
