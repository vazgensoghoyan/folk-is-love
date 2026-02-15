package com.folkislove.love.repository;

import com.folkislove.love.model.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>  {
    Page<Post> findAllByTags_Id(Long tagId, Pageable pageable);
}
