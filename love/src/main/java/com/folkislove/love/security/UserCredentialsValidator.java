package com.folkislove.love.security;

import org.springframework.stereotype.Component;

@Component
public class UserCredentialsValidator {

    public void validateUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 50) {
            throw new RuntimeException("Username must be between 3 and 50 characters long");
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.length() < 10) {
            throw new RuntimeException("Password must be at least 10 characters long");
        }

        if (password.contains(" ")) {
            throw new RuntimeException("Password must not contain spaces");
        }

        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        if (!hasLower) {
            throw new RuntimeException("Password must contain at least one lowercase letter");
        }
        if (!hasUpper) {
            throw new RuntimeException("Password must contain at least one uppercase letter");
        }
        if (!hasDigit) {
            throw new RuntimeException("Password must contain at least one digit");
        }
        if (!hasSpecial) {
            throw new RuntimeException("Password must contain at least one special character");
        }
    }
}
