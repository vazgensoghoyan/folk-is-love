package com.folkislove.love.service;

import com.folkislove.love.model.Event;
import com.folkislove.love.model.Tag;
import com.folkislove.love.repository.EventRepository;
import com.folkislove.love.dto.request.EventRequest;
import com.folkislove.love.dto.response.EventResponse;
import com.folkislove.love.exception.ResourceNotFoundException;
import com.folkislove.love.mapper.EventMapper;

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
    private final EventMapper eventMapper;

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventResponse getById(Long eventId) {
        return eventMapper.toDto(findEventById(eventId));
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByTag(Long tagId) {
        return tagService
                .getTagById(tagId)
                .getEvents()
                .stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findAll().stream()
                .filter(e -> e.getDateTime().isAfter(now))
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventResponse createEvent(EventRequest request) {
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

        return eventMapper.toDto(eventRepository.save(event));
    }

    @Transactional
    public EventResponse editEvent(Long eventId, EventRequest request) {
        Event event = findEventById(eventId);
        currentUserService.checkOwnerOrAdmin(event.getAuthor().getUsername());

        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getDateTime() != null) event.setDateTime(request.getDateTime());
        if (request.getCity() != null) event.setCity(request.getCity());
        if (request.getCountry() != null) event.setCountry(request.getCountry());
        if (request.getVenue() != null) event.setVenue(request.getVenue());
        if (request.getLink() != null) event.setLink(request.getLink());
        if (request.getTagIds() != null) event.setTags(getTagsByIds(request.getTagIds()));

        return eventMapper.toDto(eventRepository.save(event));
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = findEventById(eventId);
        currentUserService.checkOwnerOrAdmin(event.getAuthor().getUsername());
        eventRepository.delete(event);
    }

    // private methods

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));
    }

    private Set<Tag> getTagsByIds(Set<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return Set.of();

        return tagIds.stream()
                .map(tagService::getTagById)
                .collect(Collectors.toSet());
    }
}
