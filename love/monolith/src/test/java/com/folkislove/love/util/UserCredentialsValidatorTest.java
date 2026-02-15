package com.folkislove.love.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserCredentialsValidatorTest {

    private UserCredentialsValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UserCredentialsValidator();
    }

    @Nested
    class UsernameValidationTests {

        @Test
        void validUsernameShouldNotThrow() {
            assertDoesNotThrow(() -> validator.validateUsername("valid_user123"));
        }

        @Test
        void usernameTooShortShouldThrow() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validateUsername("ab"));
            assertEquals("Username must be between 3 and 50 characters long", ex.getMessage());
        }

        @Test
        void usernameTooLongShouldThrow() {
            String longUsername = "a".repeat(51);
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validateUsername(longUsername));
            assertEquals("Username must be between 3 and 50 characters long", ex.getMessage());
        }

        @Test
        void nullUsernameShouldThrow() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validateUsername(null));
            assertEquals("Username must be between 3 and 50 characters long", ex.getMessage());
        }

        @Test
        void usernameWithInvalidCharactersShouldThrow() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validateUsername("InvalidUser!"));
            assertEquals("Username must be 3-50 characters: lowercase letters, digits, '_' or '-' only", ex.getMessage());
        }

        @Test
        void usernameWithUppercaseLettersShouldThrow() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validateUsername("UserName"));
            assertEquals("Username must be 3-50 characters: lowercase letters, digits, '_' or '-' only", ex.getMessage());
        }

    }

    @Nested
    class EmailValidationTests {

        @Test
        void validEmailShouldNotThrow() {
            assertDoesNotThrow(() -> validator.validateEmail("user@example.com"));
        }

        @Test
        void nullEmailShouldThrow() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validateEmail(null));
            assertEquals("Email must not be empty", ex.getMessage());
        }

        @Test
        void blankEmailShouldThrow() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validateEmail("   "));
            assertEquals("Email must not be empty", ex.getMessage());
        }

        @Test
        void invalidEmailFormatShouldThrow() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validateEmail("invalid-email"));
            assertEquals("Email is not valid", ex.getMessage());
        }

        @Test
        void emailWithInvalidDomainShouldThrow() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validateEmail("user@domain"));
            assertEquals("Email is not valid", ex.getMessage());
        }
    }

    @Nested
    class PasswordValidationTests {

        @Test
        void validPasswordShouldNotThrow() {
            assertDoesNotThrow(() -> validator.validatePassword("Abcdef1!23"));
        }

        @Test
        void passwordTooShortShouldThrow() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validatePassword("Ab1!def"));
            assertEquals("Password must be at least 10 characters long", ex.getMessage());
        }

        @Test
        void passwordWithSpacesShouldThrow() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validatePassword("Abcd 123!@#"));
            assertEquals("Password must not contain spaces", ex.getMessage());
        }

        @Test
        void passwordWithoutLowercaseShouldThrow() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validatePassword("ABCDEF123!@#"));
            assertEquals("Password must contain at least one lowercase letter", ex.getMessage());
        }

        @Test
        void passwordWithoutUppercaseShouldThrow() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validatePassword("abcdef123!@#"));
            assertEquals("Password must contain at least one uppercase letter", ex.getMessage());
        }

        @Test
        void passwordWithoutDigitShouldThrow() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validatePassword("Abcdefgh!@#"));
            assertEquals("Password must contain at least one digit", ex.getMessage());
        }

        @Test
        void passwordWithoutSpecialShouldThrow() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validatePassword("Abcdef12345"));
            assertEquals("Password must contain at least one special character", ex.getMessage());
        }

        @Test
        void nullPasswordShouldThrow() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> validator.validatePassword(null));
            assertEquals("Password must be at least 10 characters long", ex.getMessage());
        }

    }

}
