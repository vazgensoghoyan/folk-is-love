package com.folkislove.love.repository;

import com.folkislove.love.model.Tag;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long>  {
    
}
