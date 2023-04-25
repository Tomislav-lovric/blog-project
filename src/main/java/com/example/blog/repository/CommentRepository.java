package com.example.blog.repository;

import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    boolean existsByIdAndPost(Long id, Post post);

    Comment findByIdAndPost(Long id, Post post);

    List<Comment> findAllByPost(Post post);

    boolean existsByIdAndPostAndUser(Long id, Post post, User user);

    Comment findByIdAndPostAndUser(Long id, Post post, User user);
}
