package com.folkislove.love.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.folkislove.love.model.User;
import com.folkislove.love.model.User.Role;
import com.folkislove.love.repository.UserRepository;
import com.folkislove.love.security.UserCredentialsValidator;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserCredentialsValidator credentialsValidator;

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Invalid username"));

        if (user.getBanned()) {
            throw new RuntimeException("User is banned");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(user.getUsername(), user.getRole().name());
    }

    public User register(String username, String password) {
        credentialsValidator.validateUsername(username);
        credentialsValidator.validatePassword(password);

        User user = User.builder()
            .username(username)
            .passwordHash(passwordEncoder.encode(password))
            .role(Role.USER)
            .build();

        return userRepository.save(user);
    }
}
