package com.folkislove.love.util;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class UserCredentialsValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-z0-9_-]{3,50}$"
    );

    public void validateUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 50) {
            throw new RuntimeException("Username must be between 3 and 50 characters long");
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new RuntimeException("Username must be 3-50 characters: lowercase letters, digits, '_' or '-' only");
        }
    }

    public void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email must not be empty");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new RuntimeException("Email is not valid");
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
