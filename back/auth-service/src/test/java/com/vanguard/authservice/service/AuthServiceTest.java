package com.vanguard.authservice.service;

import com.vanguard.authservice.dto.AuthResponse;
import com.vanguard.authservice.dto.LoginRequest;
import com.vanguard.authservice.dto.RegisterRequest;
import com.vanguard.authservice.entity.Role;
import com.vanguard.authservice.entity.User;
import com.vanguard.authservice.repository.UserRepository;
import com.vanguard.authservice.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@test.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        testUser = new User("testuser", "test@test.com", "hashed_password", Role.POLICYHOLDER);
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        
        authService.register(registerRequest);
        
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_DuplicateUsername() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
        assertEquals("Username is already taken.", exception.getMessage());
    }

    @Test
    void testRegister_DuplicateEmail() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
        assertEquals("Email is already registered.", exception.getMessage());
    }

    @Test
    void testRegister_WithoutEmail_GeneratesDefaultEmail() {
        RegisterRequest reqWithoutEmail = new RegisterRequest();
        reqWithoutEmail.setUsername("newuser");
        reqWithoutEmail.setPassword("password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@vanguard.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");

        authService.register(reqWithoutEmail);

        org.mockito.ArgumentCaptor<User> userCaptor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("newuser", savedUser.getUsername());
        assertEquals("newuser@vanguard.com", savedUser.getEmail());
        assertEquals(Role.POLICYHOLDER, savedUser.getRole());
    }

    @Test
    void testRegister_WithAdjusterRoleAttempt_EnforcesPolicyholder() {
        RegisterRequest reqWithAdjuster = new RegisterRequest();
        reqWithAdjuster.setUsername("hacker");
        reqWithAdjuster.setPassword("password123");
        reqWithAdjuster.setRole("ADJUSTER");

        when(userRepository.existsByUsername("hacker")).thenReturn(false);
        when(userRepository.existsByEmail("hacker@vanguard.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");

        authService.register(reqWithAdjuster);

        org.mockito.ArgumentCaptor<User> userCaptor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(userRepository, atLeastOnce()).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(Role.POLICYHOLDER, savedUser.getRole());
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);
        when(jwtTokenProvider.generateToken(testUser)).thenReturn("fake-jwt-token");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(1800000L);
        
        AuthResponse response = authService.login(loginRequest);
        
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getAccessToken());
        assertEquals("POLICYHOLDER", response.getRole());
    }

    @Test
    void testLogin_InvalidPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(false);
        
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }
}
