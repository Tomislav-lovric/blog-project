package com.example.blog.controller;

import com.example.blog.dto.CommentRequest;
import com.example.blog.dto.CommentResponse;
import com.example.blog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    @PostMapping("/{postTitle}/comments/create")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable String postTitle,
            @RequestBody @Valid CommentRequest request,
            @RequestHeader("Authorization") String bearerToken
            ) {
        return new ResponseEntity<>(service.createComment(postTitle, request, bearerToken), HttpStatus.CREATED);
    }

    //Not secured endpoint
    @GetMapping("/{postTitle}/comments/{commentId}")
    public ResponseEntity<CommentResponse> getComment(
            @PathVariable String postTitle,
            @PathVariable Long commentId
    ) {
        return ResponseEntity.ok(service.getComment(postTitle, commentId));
    }

    //Not secured endpoint
    @GetMapping("/{postTitle}/comments/all")
    public ResponseEntity<List<CommentResponse>> getAllComments(
            @PathVariable String postTitle
    ) {
        return ResponseEntity.ok(service.getAllComments(postTitle));
    }

    @PutMapping("/{postTitle}/comments/{commentId}/update")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable String postTitle,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentRequest request,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return ResponseEntity.ok(service.updateComment(postTitle, commentId, request, bearerToken));
    }

    @DeleteMapping("/{postTitle}/comments/{commentId}/delete")
    public ResponseEntity<String> deleteComment(
            @PathVariable String postTitle,
            @PathVariable Long commentId,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return ResponseEntity.ok(service.deleteComment(postTitle, commentId, bearerToken));
    }

}
