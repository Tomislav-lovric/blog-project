package com.example.blog.service;

import com.example.blog.dto.CategoryDto;
import com.example.blog.entity.Category;
import com.example.blog.exception.CategoryAlreadyExistsException;
import com.example.blog.exception.CategoryNotFoundException;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;


    public CategoryDto createCategory(CategoryDto request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new CategoryAlreadyExistsException("Category '" + request.getName() + "' already exists!");
        }

        var category = Category.builder().name(request.getName()).build();
        categoryRepository.save(category);

        return new CategoryDto(request.getName());
    }

    public String searchCategory(String category) {
        if (!categoryRepository.existsByName(category)) {
            return "Category '" + category + "' does not exist";
        }
        return "Category '" + category + "' does exists";
    }

    @Transactional
    public String updateCategory(String categoryName, CategoryDto request) {
        if (!categoryRepository.existsByName(categoryName)) {
            throw new CategoryNotFoundException("Category '" + categoryName + "' not found!");
        }
        if (categoryRepository.existsByName(request.getName())) {
            throw new CategoryAlreadyExistsException("Category '" + request.getName() + "' already exists!");
        }

        var category = categoryRepository.findByName(categoryName);
        category.setName(request.getName());

        return "Category '" + categoryName + "' changed/updated to '" + request.getName() + "'";
    }

    @Transactional
    public String deleteCategory(String categoryName) {
        if (!categoryRepository.existsByName(categoryName)) {
            throw new CategoryNotFoundException("Category '" + categoryName + "' not found!");
        }

        categoryRepository.deleteByName(categoryName);

        return "Category '" + categoryName + "' deleted!";
    }
}
