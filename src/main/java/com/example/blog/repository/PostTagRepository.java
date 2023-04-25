package com.example.blog.repository;

import com.example.blog.entity.Post;
import com.example.blog.entity.PostTag;
import com.example.blog.entity.PostTagId;
import com.example.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, PostTagId> {

    boolean existsByPostAndTag(Post post, Tag tag);

    PostTag findByPostAndTag(Post post, Tag tag);
}
