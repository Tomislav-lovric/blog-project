package com.example.blog.controller;

import com.example.blog.dto.TagDto;
import com.example.blog.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/posts")
@RequiredArgsConstructor
public class TagController {

    private final TagService service;

    @PostMapping("/tags/create")
    public ResponseEntity<TagDto> createTag(
            @RequestBody @Valid TagDto request
    ) {
        return new ResponseEntity<>(service.createTag(request), HttpStatus.CREATED);
    }

    @PostMapping("/tags/create-multi")
    public ResponseEntity<List<TagDto>> createTags(
            @RequestBody @Valid List<TagDto> request
    ) {
        List<TagDto> tags = request.stream().map(service::createTag).toList();

        return new ResponseEntity<>(tags, HttpStatus.CREATED);
    }

    //Not secured endpoint
    @GetMapping("/tags/search")
    public ResponseEntity<String> searchTag(
            @RequestParam String tag
    ) {
        return ResponseEntity.ok(service.searchTag(tag));
    }

    //Not secured endpoint
    @GetMapping("/tags/all")
    public ResponseEntity<List<TagDto>> getAllTags() {
        return ResponseEntity.ok(service.getAllTags());
    }

    @PutMapping("/tags/update/{tagName}")
    public ResponseEntity<String> updateTag(
            @PathVariable String tagName,
            @RequestBody @Valid TagDto request
    ) {
        return ResponseEntity.ok(service.updateTag(tagName, request));
    }

    @DeleteMapping("/tags/delete/{tagName}")
    public ResponseEntity<String> deleteTag(
            @PathVariable String tagName
    ) {
        return ResponseEntity.ok(service.deleteTag(tagName));
    }

    @PutMapping("/{postTitle}/tags/add")
    public ResponseEntity<String> addTagToPost(
            @PathVariable String postTitle,
            @RequestBody @Valid TagDto request,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return ResponseEntity.ok(service.addTagToPost(postTitle, request, bearerToken));
    }

    @PutMapping("/{postTitle}/tags/add-multi")
    public ResponseEntity<String> addTagsToPost(
            @PathVariable String postTitle,
            @RequestBody @Valid List<TagDto> request,
            @RequestHeader("Authorization") String bearerToken
    ) {
        request.forEach(tagDto -> service.addTagToPost(postTitle, tagDto, bearerToken));
        return ResponseEntity.ok("All tags added to the post '" + postTitle + "'");
    }

    @DeleteMapping("/{postTitle}/tags/delete")
    public ResponseEntity<String> deleteTagFromPost(
            @PathVariable String postTitle,
            @RequestBody @Valid TagDto request,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return ResponseEntity.ok(service.deleteTagFromPost(postTitle, request, bearerToken));
    }
}
