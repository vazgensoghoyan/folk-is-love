package com.folkislove.love.service;

import com.folkislove.love.model.Event;
import com.folkislove.love.model.Tag;
import com.folkislove.love.repository.EventRepository;
import com.folkislove.love.repository.TagRepository;
import com.folkislove.love.dto.response.EventResponse;
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
    private final TagRepository tagRepository;
    private final CurrentUserService currentUserService;
    private final EventMapper eventMapper;

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found: " + eventId));
        return eventMapper.toDto(event);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found: " + tagId));

        return tag.getEvents().stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getUpcomingEvents() {
        return eventRepository.findAll().stream()
                .filter(e -> e.getDateTime().isAfter(LocalDateTime.now()))
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventResponse createEvent(
            String title,
            String description,
            LocalDateTime dateTime,
            String city,
            String country,
            String venue,
            String link,
            Set<Long> tagIds
    ) {
        Set<Tag> tags = tagIds.stream()
                .map(id -> tagRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tag not found: " + id)))
                .collect(Collectors.toSet());

        Event event = Event.builder()
                .title(title)
                .description(description)
                .dateTime(dateTime)
                .city(city)
                .country(country)
                .venue(venue)
                .link(link)
                .author(currentUserService.getCurrentUser())
                .tags(tags)
                .build();

        Event saved = eventRepository.save(event);
        return eventMapper.toDto(saved);
    }

    @Transactional
    public EventResponse editEvent(
            Long eventId,
            String title,
            String description,
            LocalDateTime dateTime,
            String city,
            String country,
            String venue,
            String link,
            Set<Long> tagIds
    ) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found: " + eventId));

        currentUserService.checkOwnerOrAdmin(event.getAuthor().getUsername());

        if (title != null) event.setTitle(title);
        if (description != null) event.setDescription(description);
        if (dateTime != null) event.setDateTime(dateTime);
        if (city != null) event.setCity(city);
        if (country != null) event.setCountry(country);
        if (venue != null) event.setVenue(venue);
        if (link != null) event.setLink(link);

        if (tagIds != null) {
            Set<Tag> tags = tagIds.stream()
                    .map(id -> tagRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Tag not found: " + id)))
                    .collect(Collectors.toSet());
            event.setTags(tags);
        }

        Event saved = eventRepository.save(event);
        return eventMapper.toDto(saved);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found: " + eventId));

        currentUserService.checkOwnerOrAdmin(event.getAuthor().getUsername());

        eventRepository.delete(event);
    }
}
