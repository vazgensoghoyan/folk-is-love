package com.folkislove.love.service;

import com.folkislove.love.dto.response.TagResponse;
import com.folkislove.love.exception.ResourceNotFoundException;
import com.folkislove.love.mapper.TagMapper;
import com.folkislove.love.model.Tag;
import com.folkislove.love.repository.TagRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public Tag getTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", id));
    }

    public Tag getTagOrThrow(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository
                .findAll()
                .stream()
                .toList();
    }

    public TagResponse createTag(String name) {
        currentUserService.checkIsAdmin();

        String normalized = normalize(name);

        if (tagRepository.existsByNameIgnoreCase(normalized)) {
            throw new IllegalArgumentException("Tag already exists");
        }

        Tag tag = Tag.builder()
                .name(normalized)
                .build();

        Tag response = tagRepository.save(tag);
        return tagMapper.toDto(response);
    }

    public Tag renameTag(Long tagId, String newName) {
        currentUserService.checkIsAdmin();

        Tag tag = getTagOrThrow(tagId);
        String normalized = normalize(newName);

        if (tagRepository.existsByNameIgnoreCase(normalized)) {
            throw new IllegalArgumentException("Another tag with this name already exists");
        }

        tag.setName(normalized);
        return tag;
    }

    public void deleteTag(Long tagId) {
        currentUserService.checkIsAdmin();

        Tag tag = getTagOrThrow(tagId);

        if (!tag.getPosts().isEmpty() ||
            !tag.getEvents().isEmpty() ||
            !tag.getUsers().isEmpty()) {
            throw new IllegalStateException("Cannot delete tag that is in use. Merge it instead.");
        }

        tagRepository.delete(tag);
    }

    private String normalize(String name) {
        return name.trim();
    }
}
