package com.folkislove.love.repository;

import com.folkislove.love.model.Event;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>  {
    Page<Event> findByTags_Id(Long tagId, Pageable pageable);
    Page<Event> findByDateTimeAfter(LocalDateTime now, Pageable pagable);
}
