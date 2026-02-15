package com.folkislove.love.repository;

import com.folkislove.love.model.Event;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>  {
    List<Event> findByDateTimeAfter(LocalDateTime now);
}
