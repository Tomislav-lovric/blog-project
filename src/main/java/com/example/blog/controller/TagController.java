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

    @GetMapping("/tags/search")
    public ResponseEntity<String> searchTag(
            @RequestParam String tag
    ) {
        return ResponseEntity.ok(service.searchTag(tag));
    }

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
}
