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

    //todo Get all tags

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

    @PutMapping("/{postTitle}/categories/add")
    public ResponseEntity<String> addCategoryToPost(
            @PathVariable String postTitle,
            @RequestBody @Valid CategoryDto request,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return ResponseEntity.ok(service.addCategoryToPost(postTitle, request, bearerToken));
    }

    @PutMapping("/{postTitle}/categories/add-multi")
    public ResponseEntity<String> addCategoriesToPost(
            @PathVariable String postTitle,
            @RequestBody @Valid List<CategoryDto> request,
            @RequestHeader("Authorization") String bearerToken
    ) {
        request.forEach(categoryDto -> service.addCategoryToPost(postTitle, categoryDto, bearerToken));
        return ResponseEntity.ok("All categories added to the post '" + postTitle + "'");
    }

    @DeleteMapping("/{postTitle}/categories/delete")
    public ResponseEntity<String> deleteCategoriesFromPost(
            @PathVariable String postTitle,
            @RequestBody @Valid CategoryDto request,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return ResponseEntity.ok(service.deleteCategoryFromPost(postTitle, request, bearerToken));
    }
}
