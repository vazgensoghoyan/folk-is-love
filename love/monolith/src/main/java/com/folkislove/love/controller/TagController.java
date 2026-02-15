package com.folkislove.love.controller;

import com.folkislove.love.dto.response.TagResponse;
import com.folkislove.love.mapper.TagMapper;
import com.folkislove.love.model.Tag;
import com.folkislove.love.service.CurrentUserService;
import com.folkislove.love.service.TagService;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для тегов.
 * Админ может делать все операции.
 * Обычные пользователи могут только читать.
 */
@RestController
@RequestMapping("/api/tags")
@AllArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<Page<TagResponse>> getAllTags(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<TagResponse> tags = tagService
            .getAllTags(PageRequest.of(page, size))
            .map(tagMapper::toDto);

        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<TagResponse> getTagById(@PathVariable Long tagId) {
        Tag tag = tagService.getTagById(tagId);
        TagResponse response = tagMapper.toDto(tag);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin")
    public ResponseEntity<TagResponse> createTag(@RequestParam String name) {
        currentUserService.checkIsAdmin();
        Tag tag = tagService.createTag(name);
        TagResponse response = tagMapper.toDto(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/admin/{tagId}")
    public ResponseEntity<TagResponse> renameTag(
        @PathVariable Long tagId,
        @RequestParam String newName
    ) {
        currentUserService.checkIsAdmin();
        Tag tag = tagService.renameTag(tagId, newName);
        TagResponse response = tagMapper.toDto(tag);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long tagId) {
        currentUserService.checkIsAdmin();
        tagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }
}
