package com.folkislove.love.service;

import com.folkislove.love.model.Event;
import com.folkislove.love.model.Tag;
import com.folkislove.love.repository.EventRepository;
import com.folkislove.love.dto.request.EventRequest;
import com.folkislove.love.exception.custom.InvalidEventDateException;
import com.folkislove.love.exception.custom.ResourceNotFoundException;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final TagService tagService;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll().stream()
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsByTag(Long tagId) {
        return tagService
            .getTagById(tagId)
            .getEvents()
            .stream()
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Event> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findByDateTimeAfter(now).stream()
            .collect(Collectors.toList());
    }

    @Transactional
    public Event createEvent(EventRequest request) {

        if (!request.getDateTime().isAfter(LocalDateTime.now())) {
            throw new InvalidEventDateException();
        }

        Event event = Event.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .dateTime(request.getDateTime())
            .city(request.getCity())
            .country(request.getCountry())
            .venue(request.getVenue())
            .link(request.getLink())
            .author(currentUserService.getCurrentUser())
            .tags(getTagsByIds(request.getTagIds()))
            .build();

        return eventRepository.save(event);
    }

    @Transactional
    public Event editEvent(Long eventId, EventRequest request) {
        Event event = getEventById(eventId);
        currentUserService.checkIsOwnerOrAdmin(event.getAuthor().getUsername());
        
        if (request.getDateTime() != null) {
            if (!request.getDateTime().isAfter(LocalDateTime.now())) {
                throw new InvalidEventDateException();
            }
            event.setDateTime(request.getDateTime());
        }
        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getCity() != null) event.setCity(request.getCity());
        if (request.getCountry() != null) event.setCountry(request.getCountry());
        if (request.getVenue() != null) event.setVenue(request.getVenue());
        if (request.getLink() != null) event.setLink(request.getLink());
        if (request.getTagIds() != null) event.setTags(getTagsByIds(request.getTagIds()));


        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = getEventById(eventId);
        currentUserService.checkIsOwnerOrAdmin(event.getAuthor().getUsername());
        eventRepository.delete(event);
    }

    // private methods-

    private Set<Tag> getTagsByIds(Set<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return Set.of();

        return tagIds.stream()
            .map(tagService::getTagById)
            .collect(Collectors.toSet());
    }
}
