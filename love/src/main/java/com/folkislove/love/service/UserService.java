package com.folkislove.love.service;

import com.folkislove.love.exception.custom.ResourceNotFoundException;
import com.folkislove.love.model.Tag;
import com.folkislove.love.model.User;
import com.folkislove.love.repository.UserRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TagService tagService;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
    }

    @Transactional
    public void addInterest(Long tagId) {
        User user = currentUserService.getCurrentUser();
        Tag tag = tagService.getTagById(tagId);
        user.getInterests().add(tag);
    }

    @Transactional
    public void removeInterest(Long tagId) {
        User user = currentUserService.getCurrentUser();
        Tag tag = tagService.getTagById(tagId);
        user.getInterests().remove(tag);
    }
    
    @Transactional
    public void deleteUser(Long userId) {
        currentUserService.checkIsAdmin();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        userRepository.delete(user);
    }
}
