package com.folkislove.love.service;

import com.folkislove.love.dto.response.TagResponse;
import com.folkislove.love.exception.ResourceNotFoundException;
import com.folkislove.love.exception.TagAlreadyExistsException;
import com.folkislove.love.exception.TagInUseException;
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
    //private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public Tag getTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", id));
    }

    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository
                .findAll()
                .stream()
                .toList();
    }

    public TagResponse createTag(String tagName) {
        //currentUserService.checkIsAdmin();

        String normalized = normalize(tagName);

        if (tagRepository.existsByNameIgnoreCase(normalized)) {
            throw new TagAlreadyExistsException(tagName);
        }

        Tag tag = Tag.builder()
                .name(normalized)
                .build();

        Tag response = tagRepository.save(tag);
        return tagMapper.toDto(response);
    }

    public Tag renameTag(Long tagId, String newName) {
        //currentUserService.checkIsAdmin();

        Tag tag = getTagById(tagId);
        String normalizedName = normalize(newName);

        if (tagRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new TagAlreadyExistsException(normalizedName);
        }

        tag.setName(normalizedName);
        return tag;
    }

    public void deleteTag(Long tagId) {
        //currentUserService.checkIsAdmin();

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
