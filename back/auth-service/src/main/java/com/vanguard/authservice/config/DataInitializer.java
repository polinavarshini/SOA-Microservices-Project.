package com.vanguard.authservice.config;

import com.vanguard.authservice.entity.Role;
import com.vanguard.authservice.entity.User;
import com.vanguard.authservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("demo_adjuster")) {
                User adjuster = new User(
                        "demo_adjuster",
                        "adjuster@vanguard.com",
                        passwordEncoder.encode("adjuster123"),
                        Role.ADJUSTER
                );
                userRepository.save(adjuster);
            }
            if (!userRepository.existsByUsername("demo_policyholder")) {
                User policyholder = new User(
                        "demo_policyholder",
                        "policyholder@vanguard.com",
                        passwordEncoder.encode("policyholder123"),
                        Role.POLICYHOLDER
                );
                userRepository.save(policyholder);
            }
        };
    }
}
