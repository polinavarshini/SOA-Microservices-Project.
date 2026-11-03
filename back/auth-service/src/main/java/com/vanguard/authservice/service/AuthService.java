package com.vanguard.authservice.service;

import com.vanguard.authservice.dto.AuthResponse;
import com.vanguard.authservice.dto.LoginRequest;
import com.vanguard.authservice.dto.RegisterRequest;
import com.vanguard.authservice.entity.Role;
import com.vanguard.authservice.entity.User;
import com.vanguard.authservice.exception.UserAlreadyExistsException;
import com.vanguard.authservice.repository.UserRepository;
import com.vanguard.authservice.security.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void register(RegisterRequest request) {
        String username = request.getUsername() != null ? request.getUsername().trim() : "";
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Username is already taken.");
        }

        String email = request.getEmail();
        if (email == null || email.isBlank()) {
            email = username.toLowerCase() + "@vanguard.com";
        } else {
            email = email.trim();
        }

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email is already registered.");
        }

        User user = new User(
                username,
                email,
                passwordEncoder.encode(request.getPassword()),
                Role.POLICYHOLDER // Public registration only creates policyholders
        );

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password.");
        }

        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, jwtTokenProvider.getExpirationMs() / 1000, user.getRole().name());
    }
}
