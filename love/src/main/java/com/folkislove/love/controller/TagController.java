package com.folkislove.love.controller;

import com.folkislove.love.model.Tag;
import com.folkislove.love.service.CurrentUserService;
import com.folkislove.love.service.TagService;

import lombok.AllArgsConstructor;

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
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<Tag> getTagById(@PathVariable Long tagId) {
        Tag tag = tagService.getTagById(tagId);
        return ResponseEntity.ok(tag);
    }

    @PostMapping("/admin")
    public ResponseEntity<Tag> createTag(@RequestParam String name) {
        currentUserService.checkIsAdmin();
        Tag tag = tagService.createTag(name);
        return ResponseEntity.ok(tag);
    }

    @PutMapping("/admin/{tagId}")
    public ResponseEntity<Tag> renameTag(
        @PathVariable Long tagId,
        @RequestParam String newName
    ) {
        currentUserService.checkIsAdmin();
        Tag tag = tagService.renameTag(tagId, newName);
        return ResponseEntity.ok(tag);
    }

    @DeleteMapping("/admin/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long tagId) {
        currentUserService.checkIsAdmin();
        tagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }
}
