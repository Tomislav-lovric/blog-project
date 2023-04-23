package com.example.blog.controller;

import com.example.blog.dto.CategoryDto;
import com.example.blog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/posts")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @PostMapping("/categories/create")
    public ResponseEntity<CategoryDto> createCategory(
            @RequestBody @Valid CategoryDto request
    ) {
        return new ResponseEntity<>(service.createCategory(request), HttpStatus.CREATED);
    }

    @PostMapping("/categories/create-multi")
    public ResponseEntity<List<CategoryDto>> createCategories(
            @RequestBody @Valid List<CategoryDto> request
    ) {
        List<CategoryDto> categories = request.stream()
                .map(service::createCategory)
                .toList();

        return new ResponseEntity<>(categories, HttpStatus.CREATED);
    }

    @GetMapping("/categories/search")
    public ResponseEntity<String> searchCategory(
            @RequestParam String category
    ) {
        return ResponseEntity.ok(service.searchCategory(category));
    }

    @PutMapping("/categories/update/{categoryName}")
    public ResponseEntity<String> updateCategory(
            @PathVariable String categoryName,
            @RequestBody @Valid CategoryDto request
    ) {
        return ResponseEntity.ok(service.updateCategory(categoryName, request));
    }

    @DeleteMapping("/categories/delete/{categoryName}")
    public ResponseEntity<String> deleteCategory(
            @PathVariable String categoryName
    ) {
        return ResponseEntity.ok(service.deleteCategory(categoryName));
    }
}
