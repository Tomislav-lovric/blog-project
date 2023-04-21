package com.example.blog.repository;

import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    boolean existsByTitleAndUser(String title, User user);

    Post findByTitleAndUser(String title, User user);

    boolean existsByTitle(String title);

    Post findByTitle(String title);

    void deleteByTitleAndUser(String title, User user);
}
