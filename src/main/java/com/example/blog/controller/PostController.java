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
    public ResponseEntity<PostResponse> create(
            @RequestBody @Valid PostRequest request,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return new ResponseEntity<>(service.create(request, bearerToken), HttpStatus.CREATED);
    }

}
