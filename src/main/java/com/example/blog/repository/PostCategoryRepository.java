package com.example.blog.repository;

import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import com.example.blog.entity.PostCategory;
import com.example.blog.entity.PostCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, PostCategoryId> {

    boolean existsByPostAndCategory(Post post, Category category);

    PostCategory findByPostAndCategory(Post post, Category category);

    List<PostCategory> findByPost(Post post);
}
