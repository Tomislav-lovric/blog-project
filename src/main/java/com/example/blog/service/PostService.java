package com.example.blog.service;

import com.example.blog.dto.PostRequest;
import com.example.blog.dto.PostResponse;
import com.example.blog.entity.Post;
import com.example.blog.exception.PostTitleAlreadyExistsException;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public PostResponse create(PostRequest request, String bearerToken) {
        // username is users email in our case
        String username = jwtService.extractUsername(bearerToken);
        var user = userRepository.findUserByEmail(username);

        if (postRepository.existsByTitleAndUser(request.getTitle(), user)) {
            throw new PostTitleAlreadyExistsException("Post with that title already exists, please use different title");
        }

        //todo fix this part by making our dto accept categories and tags
        // use Set in said dto to make them unique and also make necessary
        // checks (does category exists, does tag exists etc.)

        // Categories and Tags need to be added later (using their endpoints)
        var post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();

        return null;
    }

}
