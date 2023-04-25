package com.example.blog.service;

import com.example.blog.dto.CategoryDto;
import com.example.blog.entity.Category;
import com.example.blog.entity.PostCategory;
import com.example.blog.entity.PostCategoryId;
import com.example.blog.exception.*;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.PostCategoryRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PostCategoryRepository postCategoryRepository;


    //Most of these methods are simple methods for CRUD operations so there is no need
    //to explain what they do, especially since they are not using any difficult logic
    //only last two methods will be explained somewhat
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

    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream().map(category -> new CategoryDto(category.getName())).toList();
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

    //Unlike previous methods here we need to use bridge table which makes this method
    //look somewhat more complicated (which it isn't)
    @Transactional
    public String addCategoryToPost(String postTitle, CategoryDto request, String bearerToken) {
        //Unlike previous methods here we are editing posts and only users that created that post
        //can edit it, that is why we are fetching user by getting token from header and
        //extracting username (email in our case) from it and using it to get user
        //(we do not need to check if user exists in our db since if he does not exist
        //the token will fail)
        String username = jwtService.extractUsername(bearerToken.substring(7));
        var user = userRepository.findUserByEmail(username);

        if (!postRepository.existsByTitleAndUser(postTitle, user)) {
            throw new PostNotFoundException("Post with the title '" + postTitle + "' not found!");
        }
        if (!categoryRepository.existsByName(request.getName())) {
            throw new CategoryNotFoundException("Category '" + request.getName() + "' not found!");
        }

        //After we did the check we get post and category
        var post = postRepository.findByTitleAndUser(postTitle, user);
        var category = categoryRepository.findByName(request.getName());

        //Which we then use to check if they are connected. If they are not throw exception, else continue
        if (postCategoryRepository.existsByPostAndCategory(post, category)) {
            throw new PostAlreadyContainsThatCategoryException(
                    "Post '" + postTitle + "' already contains category '" + request.getName() + "'"
            );
        }

        //If it passes we get PostCategory from our Post to add the new PostCategory, and save our post
        // (basically we are connecting our post with the category using bridge table)
        var postCategories = post.getPostCategories();
        var postCategoryToAdd = PostCategory.builder()
                .postCategoryId(new PostCategoryId(post.getId(), category.getId()))
                .post(post)
                .category(category)
                .build();

        postCategories.add(postCategoryToAdd);
        post.setPostCategories(postCategories);
        postRepository.save(post);

        return request.getName() + " category added to the post '" + postTitle + "'";
    }

    //This one is very similar to the method before it, with just couple of changes
    @Transactional
    public String deleteCategoryFromPost(String postTitle, CategoryDto request, String bearerToken) {
        String username = jwtService.extractUsername(bearerToken.substring(7));
        var user = userRepository.findUserByEmail(username);

        if (!postRepository.existsByTitleAndUser(postTitle, user)) {
            throw new PostNotFoundException("Post with the title '" + postTitle + "' not found!");
        }
        if (!categoryRepository.existsByName(request.getName())) {
            throw new CategoryNotFoundException("Category '" + request.getName() + "' not found!");
        }

        var post = postRepository.findByTitleAndUser(postTitle, user);
        var category = categoryRepository.findByName(request.getName());

        //Unlike before, here we check if connection does not exist (because we need it to exist
        //to be able to remove/delete it), if it does not exist throw exception
        if (!postCategoryRepository.existsByPostAndCategory(post, category)) {
            throw new PostDoesNotContainThatCategoryException(
                    "Post '" + postTitle + "' does not contain category '" + request.getName() + "'"
            );
        }

        //Then just like before we get our PostCategory from our post, however unlike before we do not
        //add the new connection to our bridge table but rather remove the category from our post (PostCategory)
        //and then save that updated post again to our db
        var postCategories = post.getPostCategories();
        var postCategoryToRemove = postCategoryRepository.findByPostAndCategory(post, category);

        postCategories.remove(postCategoryToRemove);
        post.setPostCategories(postCategories);
        postRepository.save(post);

        return request.getName() + " category removed from the post '" + postTitle + "'";
    }
}
