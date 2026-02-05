package com.folkislove.love.service.admin;

import com.folkislove.love.model.Tag;
import com.folkislove.love.model.Post;
import com.folkislove.love.model.Event;
import com.folkislove.love.model.User;
import com.folkislove.love.repository.TagRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceAdmin {

    private final TagRepository tagRepository;

    public Tag createTag(String name) {
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
        Tag tag = getTagOrThrow(tagId);
        String normalized = normalize(newName);

        if (tagRepository.existsByNameIgnoreCase(normalized)) {
            throw new IllegalArgumentException("Another tag with this name already exists");
        }

        tag.setName(normalized);
        return tag;
    }

    public void deleteTag(Long tagId) {
        Tag tag = getTagOrThrow(tagId);

        if (!tag.getPosts().isEmpty() ||
            !tag.getEvents().isEmpty() ||
            !tag.getUsers().isEmpty()) {
            throw new IllegalStateException("Cannot delete tag that is in use. Merge it instead.");
        }

        tagRepository.delete(tag);
    }

    public void mergeTags(Long sourceTagId, Long targetTagId) {
        if (sourceTagId.equals(targetTagId)) {
            throw new IllegalArgumentException("Cannot merge the same tag");
        }

        Tag source = getTagOrThrow(sourceTagId);
        Tag target = getTagOrThrow(targetTagId);

        for (Post post : new HashSet<>(source.getPosts())) {
            post.getTags().remove(source);
            post.getTags().add(target);
        }

        for (Event event : new HashSet<>(source.getEvents())) {
            event.getTags().remove(source);
            event.getTags().add(target);
        }

        for (User user : new HashSet<>(source.getUsers())) {
            user.getInterests().remove(source);
            user.getInterests().add(target);
        }

        tagRepository.delete(source);
    }

    private Tag getTagOrThrow(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + id));
    }

    private String normalize(String name) {
        return name.trim();
    }
}
