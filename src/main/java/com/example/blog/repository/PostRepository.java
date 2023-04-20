package com.example.blog.repository;

import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    boolean existsByTitleAndUser(String title, User user);

    void deleteByTitleAndUser(String title, User user);
}
