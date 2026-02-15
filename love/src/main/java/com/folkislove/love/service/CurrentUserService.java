package com.folkislove.love.service;

import com.folkislove.love.exception.AccessDeniedException;
import com.folkislove.love.exception.AuthorizationException;
import com.folkislove.love.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
    }

    public boolean isAdmin() {
        User user = getCurrentUser();
        return user.getRole() == User.Role.ADMIN;
    }

    public boolean isOwner(String username) {
        return getCurrentUsername().equals(username);
    }

    public boolean isOwnerOrAdmin(String username) {
        return isOwner(username) || isAdmin();
    }

    public void checkIsAdmin() {
        if (!isAdmin()) {
            throw new AccessDeniedException("You are not admin");
        }
    }

    public void checkIsOwner(String username) {
        if (!isOwner(username)) {
            throw new AccessDeniedException("You are not owner of this resource");
        }
    }

    public void checkIsOwnerOrAdmin(String username) {
        if (!isOwnerOrAdmin(username)) {
            throw new AccessDeniedException("You don't have permission to access this resource");
        }
    }
    
    // private helper method to get Authentication object

    private Authentication getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AuthorizationException("User is not authenticated");
        }
        return auth;
    }
}
