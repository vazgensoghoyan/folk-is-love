package com.folkislove.love.service;

import com.folkislove.love.model.Tag;
import com.folkislove.love.model.User;
import com.folkislove.love.repository.TagRepository;
import com.folkislove.love.repository.UserRepository;
import com.folkislove.love.dto.UserResponse;
import com.folkislove.love.mapper.UserMapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final CurrentUserService currentUserService;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        User user = currentUserService.getCurrentUser();
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }

    @Transactional
    public void addInterest(Long tagId) {
        User user = currentUserService.getCurrentUser();
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        user.getInterests().add(tag);
        userRepository.save(user); // TODO: избыточно ли?
    }

    @Transactional
    public void removeInterest(Long tagId) {
        User user = currentUserService.getCurrentUser();
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        user.getInterests().remove(tag);
        userRepository.save(user); // TODO: избыточно ли?
    }
}
