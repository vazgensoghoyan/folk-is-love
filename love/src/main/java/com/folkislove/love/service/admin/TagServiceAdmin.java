package com.folkislove.love.service.admin;

import com.folkislove.love.model.Tag;
import com.folkislove.love.repository.TagRepository;
import com.folkislove.love.service.CurrentUserService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceAdmin {

    private final TagRepository tagRepository;
    private final CurrentUserService currentUserService;

    public Tag createTag(String name) {
        checkCurrentIsAdmin();

        String normalized = normalize(name);

        if (tagRepository.existsByNameIgnoreCase(normalized)) {
            throw new IllegalArgumentException("Tag already exists");
        }

        Tag tag = Tag.builder()
                .name(normalized)
                .build();

        return tagRepository.save(tag);
    }

    public Tag renameTag(Long tagId, String newName) {
        checkCurrentIsAdmin();

        Tag tag = getTagOrThrow(tagId);
        String normalized = normalize(newName);

        if (tagRepository.existsByNameIgnoreCase(normalized)) {
            throw new IllegalArgumentException("Another tag with this name already exists");
        }

        tag.setName(normalized);
        return tag;
    }

    public void deleteTag(Long tagId) {
        checkCurrentIsAdmin();

        Tag tag = getTagOrThrow(tagId);

        if (!tag.getPosts().isEmpty() ||
            !tag.getEvents().isEmpty() ||
            !tag.getUsers().isEmpty()) {
            throw new IllegalStateException("Cannot delete tag that is in use. Merge it instead.");
        }

        tagRepository.delete(tag);
    }

    // private helpers

    private Tag getTagOrThrow(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + id));
    }

    private String normalize(String name) {
        return name.trim();
    }
    
    private void checkCurrentIsAdmin() {
        if (!currentUserService.isAdmin()) {
            throw new RuntimeException("You don't have permission to access this resource");
        }
    }
}
