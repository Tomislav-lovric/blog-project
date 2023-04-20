package com.example.blog.controller;

import com.example.blog.dto.PostRequest;
import com.example.blog.dto.PostResponse;
import com.example.blog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{postTitle}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable String postTitle,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return ResponseEntity.ok(service.getPost(postTitle, bearerToken));
    }

    @DeleteMapping("/{postTitle}")
    public ResponseEntity<String> deletePost(
            @PathVariable String postTitle,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return ResponseEntity.ok(service.deletePost(postTitle, bearerToken));
    }

}
