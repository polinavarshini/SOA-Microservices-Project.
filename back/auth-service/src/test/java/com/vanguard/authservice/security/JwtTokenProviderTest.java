package com.vanguard.authservice.security;

import com.vanguard.authservice.entity.Role;
import com.vanguard.authservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private User user;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        // 256-bit hex for testing
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 1800000L); // 30 minutes
        jwtTokenProvider.init();

        user = new User("testuser", "test@test.com", "hash", Role.POLICYHOLDER);
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
    }

    @Test
    void testGenerateToken() {
        String token = jwtTokenProvider.generateToken(user);
        assertNotNull(token);
        assertTrue(token.length() > 20);
    }
}
