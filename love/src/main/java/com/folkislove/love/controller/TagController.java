package com.folkislove.love.controller;

import com.folkislove.love.dto.response.TagResponse;
import com.folkislove.love.mapper.TagMapper;
import com.folkislove.love.model.Tag;
import com.folkislove.love.service.CurrentUserService;
import com.folkislove.love.service.TagService;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<TagResponse>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        List<TagResponse> response = tags.stream().map(tagMapper::toDto).toList();
        return ResponseEntity.ok(response);
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
        TagResponse tag = tagService.createTag(name);
        return ResponseEntity.status(HttpStatus.CREATED).body(tag);
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
