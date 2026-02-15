package com.folkislove.love.service;

import com.folkislove.love.exception.custom.ResourceNotFoundException;
import com.folkislove.love.exception.custom.TagAlreadyExistsException;
import com.folkislove.love.exception.custom.TagInUseException;
import com.folkislove.love.model.Tag;
import com.folkislove.love.repository.TagRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public Tag getTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", id));
    }

    @Transactional(readOnly = true)
    public Page<Tag> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    public Tag createTag(String tagName) {
        currentUserService.checkIsAdmin();

        String normalized = normalize(tagName);

        if (tagRepository.existsByNameIgnoreCase(normalized)) {
            throw new TagAlreadyExistsException(tagName);
        }

        Tag tag = Tag.builder()
                .name(normalized)
                .build();

        return tagRepository.save(tag);
    }

    public Tag renameTag(Long tagId, String newName) {
        currentUserService.checkIsAdmin();

        Tag tag = getTagById(tagId);
        String normalizedName = normalize(newName);

        if (tagRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new TagAlreadyExistsException(normalizedName);
        }

        tag.setName(normalizedName);
        return tag;
    }

    public void deleteTag(Long tagId) {
        currentUserService.checkIsAdmin();

        Tag tag = getTagById(tagId);

        if (!tag.getPosts().isEmpty() ||
            !tag.getEvents().isEmpty() ||
            !tag.getUsers().isEmpty()) {
            throw new TagInUseException(tag.getName());
        }

        tagRepository.delete(tag);
    }

    private String normalize(String name) {
        return name.trim();
    }
}
