package com.folkislove.love.controller;

import com.folkislove.love.dto.request.EventRequest;
import com.folkislove.love.dto.response.EventResponse;
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
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventRequest request) {
        EventResponse response = eventService.createEvent(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> editEvent(
            @PathVariable Long id,
            @RequestBody EventRequest request
    ) {
        String authorUsername = eventService.getById(id).getAuthorUsername();
        if (currentUserService.isOwnerOrAdmin(authorUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        EventResponse response = eventService.editEvent(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        String authorUsername = eventService.getById(id).getAuthorUsername();
        if (currentUserService.isOwnerOrAdmin(authorUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<EventResponse>> getEventsByTag(@PathVariable Long tagId) {
        return ResponseEntity.ok(eventService.getEventsByTag(tagId));
    }
}
