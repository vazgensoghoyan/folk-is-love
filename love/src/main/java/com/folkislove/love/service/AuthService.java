package com.folkislove.love.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.folkislove.love.exception.EmailAlreadyRegisteredException;
import com.folkislove.love.dto.request.AuthRequest;
import com.folkislove.love.dto.request.RegisterRequest;
import com.folkislove.love.exception.AuthorizationException;
import com.folkislove.love.exception.UsernameAlreadyTakenException;
import com.folkislove.love.model.User;
import com.folkislove.love.model.User.Role;
import com.folkislove.love.repository.UserRepository;
import com.folkislove.love.util.UserCredentialsValidator;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserCredentialsValidator credentialsValidator;

    public String login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new AuthorizationException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthorizationException("Invalid username or password");
        }

        return jwtService.generateToken(user.getUsername(), user.getRole().name());
    }

    public User register(RegisterRequest request) {
        String username = request.getUsername();
        String email = request.getEmail();
        String password = request.getPassword();

        credentialsValidator.validateUsername(username);
        credentialsValidator.validateEmail(email);
        credentialsValidator.validatePassword(password);

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyTakenException(username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }

        User user = User.builder()
            .username(username)
            .email(email)
            .passwordHash(passwordEncoder.encode(password))
            .role(Role.USER)
            .build();

        return userRepository.save(user);
    }
}
