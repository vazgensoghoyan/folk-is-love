package com.folkislove.love.repository;

import com.folkislove.love.model.Event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>  {
    
}
