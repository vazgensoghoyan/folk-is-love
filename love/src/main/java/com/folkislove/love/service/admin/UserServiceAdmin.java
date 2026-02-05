package com.folkislove.love.service.admin;

import com.folkislove.love.model.User;
import com.folkislove.love.repository.UserRepository;
import com.folkislove.love.service.CurrentUserService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserServiceAdmin {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public void deleteUser(Long userId) {
        checkCurrentIsAdmin();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    private void checkCurrentIsAdmin() {
        if (!currentUserService.isAdmin()) {
            throw new RuntimeException("You don't have permission to access this resource");
        }
    }
}
