package com.folkislove.love.controller;

import com.folkislove.common.dto.request.EventRequest;
import com.folkislove.common.dto.response.EventResponse;
import com.folkislove.love.mapper.EventMapper;
import com.folkislove.love.model.Event;
import com.folkislove.love.service.EventService;

import jakarta.validation.Valid;

import com.folkislove.love.service.CurrentUserService;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<EventResponse> response = eventService
            .getAllEvents(PageRequest.of(page, size))
            .map(eventMapper::toDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
        @Valid @RequestBody EventRequest request
    ) {
        Event event = eventService.createEvent(request);
        EventResponse response = eventMapper.toDto(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        EventResponse response = eventMapper.toDto(event);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> editEvent(
        @PathVariable Long id,
        @Valid @RequestBody EventRequest request
    ) {
        String authorUsername = getAuthorUsernameById(id);

        currentUserService.checkIsOwner(authorUsername);

        Event event = eventService.editEvent(id, request);
        EventResponse response = eventMapper.toDto(event);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        String authorUsername = getAuthorUsernameById(id);

        currentUserService.checkIsOwnerOrAdmin(authorUsername);

        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/upcoming")
    public ResponseEntity<Page<EventResponse>> getUpcomingEvents(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<EventResponse> response = eventService
            .getUpcomingEvents(PageRequest.of(page, size))
            .map(eventMapper::toDto);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<Page<EventResponse>> getEventsByTag(
        @PathVariable Long tagId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<EventResponse> response = eventService
            .getEventsByTag(tagId, PageRequest.of(page, size))
            .map(eventMapper::toDto);

        return ResponseEntity.ok(response);
    }

    // private helper

    private String getAuthorUsernameById(Long id) {
        return eventService.getEventById(id).getAuthor().getUsername();
    }
}
