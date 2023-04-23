package com.example.blog.controller;

import com.example.blog.dto.PostRequest;
import com.example.blog.dto.PostResponse;
import com.example.blog.dto.PostUpdateRequest;
import com.example.blog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService service;

    @PostMapping("/create")
    public ResponseEntity<PostResponse> createPost(
            @RequestBody @Valid PostRequest request,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return new ResponseEntity<>(service.createPost(request, bearerToken), HttpStatus.CREATED);
    }

    //This endpoint is not secured, so anyone can see posts
    @GetMapping("/{postTitle}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable String postTitle
    ) {
        return ResponseEntity.ok(service.getPost(postTitle));
    }

    //This endpoint is not secured, so anyone can see all posts
    @GetMapping("/all")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(service.getAllPosts());
    }

    @PutMapping("/update/{postTitle}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable String postTitle,
            @RequestBody @Valid PostUpdateRequest request,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return ResponseEntity.ok(service.updatePost(postTitle, request, bearerToken));
    }

    @DeleteMapping("/{postTitle}")
    public ResponseEntity<String> deletePost(
            @PathVariable String postTitle,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return ResponseEntity.ok(service.deletePost(postTitle, bearerToken));
    }

    //todo Maybe make an endpoints for searching for posts based on their categories or tags

}
