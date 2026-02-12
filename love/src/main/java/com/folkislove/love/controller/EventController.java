package com.folkislove.love.controller;

import com.folkislove.love.dto.request.EventRequest;
import com.folkislove.love.dto.response.EventResponse;
import com.folkislove.love.service.EventService;
import com.folkislove.love.service.CurrentUserService;

import lombok.AllArgsConstructor;
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

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<EventResponse>> getEventsByTag(@PathVariable Long tagId) {
        return ResponseEntity.ok(eventService.getEventsByTag(tagId));
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventRequest request) {
        EventResponse response = eventService.createEvent(
                request.getTitle(),
                request.getDescription(),
                request.getDateTime(),
                request.getCity(),
                request.getCountry(),
                request.getVenue(),
                request.getLink(),
                request.getTagIds()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> editEvent(
            @PathVariable Long id,
            @RequestBody EventRequest request
    ) {
        EventResponse response = eventService.editEvent(
                id,
                request.getTitle(),
                request.getDescription(),
                request.getDateTime(),
                request.getCity(),
                request.getCountry(),
                request.getVenue(),
                request.getLink(),
                request.getTagIds()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
