package com.folkislove.love.repository;

import com.folkislove.love.model.Comment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>  {
    
}
