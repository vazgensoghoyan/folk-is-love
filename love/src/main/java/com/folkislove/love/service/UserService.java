package com.folkislove.love.service;

import com.folkislove.love.model.Tag;
import com.folkislove.love.model.User;
import com.folkislove.love.repository.TagRepository;
import com.folkislove.love.repository.UserRepository;
import com.folkislove.love.dto.UserResponse;
import com.folkislove.love.mapper.UserMapper;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final CurrentUserService currentUserService;
    private final UserMapper userMapper;

    // Получение профиля текущего пользователя
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        User user = currentUserService.getCurrentUser();
        return userMapper.toDto(user);
    }

    // Получение профиля по username
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }

    // Добавление интереса
    @Transactional
    public void addInterest(Long tagId) {
        User user = currentUserService.getCurrentUser();
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        user.getInterests().add(tag);
        userRepository.save(user);
    }

    // Удаление интереса
    @Transactional
    public void removeInterest(Long tagId) {
        User user = currentUserService.getCurrentUser();
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        user.getInterests().remove(tag);
        userRepository.save(user);
    }

    // Получение всех пользователей (только админ)
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(Pageable pageable) {
        checkCurrentIsAdmin();

        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    // Удаление пользователя (только админ)
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
