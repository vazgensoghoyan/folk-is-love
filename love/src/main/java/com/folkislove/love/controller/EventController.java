package com.folkislove.love.controller;

import com.folkislove.love.dto.request.EventRequest;
import com.folkislove.love.dto.response.EventResponse;
import com.folkislove.love.mapper.EventMapper;
import com.folkislove.love.model.Event;
import com.folkislove.love.service.EventService;
import com.folkislove.love.service.CurrentUserService;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> response = eventService
            .getAllEvents()
            .stream()
            .map(eventMapper::toDto)
            .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventRequest request) {
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
            @RequestBody EventRequest request
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
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        List<EventResponse> response = eventService
            .getUpcomingEvents()
            .stream()
            .map(eventMapper::toDto)
            .toList();
    
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<EventResponse>> getEventsByTag(@PathVariable Long tagId) {
        List<EventResponse> response = eventService
            .getEventsByTag(tagId)
            .stream()
            .map(eventMapper::toDto)
            .toList();

        return ResponseEntity.ok(response);
    }

    // private helper

    private String getAuthorUsernameById(Long id) {
        return eventService.getEventById(id).getAuthor().getUsername();
    }
}
