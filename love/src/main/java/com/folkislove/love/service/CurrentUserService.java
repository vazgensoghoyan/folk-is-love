package com.folkislove.love.service;

import com.folkislove.love.model.User;
import com.folkislove.love.repository.UserRepository;

import lombok.AllArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public String getCurrentUsername() {
        Authentication auth = getAuthentication();
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return auth.getName();
    }

    public User getCurrentUser() {
        String username = getCurrentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean isAdmin() {
        User user = getCurrentUser();
        return user.getRole() == User.Role.ADMIN;
    }

    public boolean isOwnerOrAdmin(String username) {
        return getCurrentUsername().equals(username) || isAdmin();
    }

    public void checkOwnerOrAdmin(String username) {
        if (!isOwnerOrAdmin(username)) {
            throw new RuntimeException("You don't have permission to access this resource");
        }
    }
    
    // private helper method to get Authentication object

    private Authentication getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("User is not authenticated");
        }
        return auth;
    }
}
