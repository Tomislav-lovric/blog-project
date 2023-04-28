package com.example.blog.service;

import com.example.blog.dto.CategoryDto;
import com.example.blog.entity.*;
import com.example.blog.exception.*;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.PostCategoryRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private static final String TOKEN = "bearer token";

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PostCategoryRepository postCategoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDto categoryDto;
    private Post post;
    private User user;
    private PostCategory postCategory;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .firstName("John")
                .lastName("Evans")
                .email("john_evans@gmail.com")
                .password("Test.123")
                .repeatPassword("Test.123")
                .role(Role.USER)
                .build();

        post = Post.builder()
                .title("Test")
                .content("test")
                .createdAt(LocalDateTime.now().withNano(0))
                .updatedAt(LocalDateTime.now().withNano(0))
                .user(user)
                .build();

        category = Category.builder().name("category").build();

        categoryDto = CategoryDto.builder().name("category").build();

        postCategory = PostCategory.builder()
                .postCategoryId(new PostCategoryId(post.getId(), category.getId()))
                .post(post)
                .category(category)
                .build();

    }

    @Test
    void testCreateCategoryShouldReturnCategoryDto() {
        // When
        when(categoryRepository.existsByName(categoryDto.getName())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto expected = categoryService.createCategory(categoryDto);

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.getName()).isEqualTo(categoryDto.getName());
    }

    @Test
    void testCreateCategoryShouldThrowCategoryAlreadyExistsException() {
        // When
        when(categoryRepository.existsByName(categoryDto.getName())).thenReturn(true);

        // Then
        assertThatThrownBy(() -> categoryService.createCategory(categoryDto))
                .isInstanceOf(CategoryAlreadyExistsException.class)
                .hasMessageContaining("Category '" + categoryDto.getName() + "' already exists!");
    }

    @Test
    void testSearchCategoryCategoryExistsShouldReturnString() {
        // Given
        String categoryName = "category";

        // When
        when(categoryRepository.existsByName(categoryName)).thenReturn(true);

        String expected = categoryService.searchCategory(categoryName);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testSearchCategoryCategoryDoesNotExistShouldReturnString() {
        // Given
        String categoryName = "category does not exist";

        // When
        when(categoryRepository.existsByName(categoryName)).thenReturn(false);

        String expected = categoryService.searchCategory(categoryName);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testGetAllCategoriesShouldReturnCategoryDtoList() {
        // When
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryDto> expected = categoryService.getAllCategories();

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.get(0).getName()).isEqualTo(category.getName());
    }

    @Test
    void testUpdateCategoryShouldReturnString() {
        // Given
        String categoryToUpdate = "category";

        // When
        //Can't use the mock of the same method twice, because it will result in error, so it needs
        //to be done this way, and since we should pass two different values to it, those being
        //categoryToUpdate and categoryDto.getName() instead we use anyString()
        when(categoryRepository.existsByName(anyString())).thenReturn(true, false);
        when(categoryRepository.findByName(categoryToUpdate)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        String expected = categoryService.updateCategory(categoryToUpdate, categoryDto);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testUpdateCategoryShouldThrowCategoryNotFoundException() {
        // Given
        String categoryToUpdate = "category not found";

        // When
        when(categoryRepository.existsByName(categoryToUpdate)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryToUpdate, categoryDto))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category '" + categoryToUpdate + "' not found!");
    }

    @Test
    void testUpdateCategoryShouldThrowCategoryAlreadyExistsException() {
        // Given
        String categoryToUpdate = "category";

        // When
        when(categoryRepository.existsByName(anyString())).thenReturn(true, true);

        // Then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryToUpdate, categoryDto))
                .isInstanceOf(CategoryAlreadyExistsException.class)
                .hasMessageContaining("Category '" + categoryToUpdate + "' already exists!");
    }

    @Test
    void testDeleteCategoryShouldReturnString() {
        // Given
        String categoryToDelete = "category";

        // When
        when(categoryRepository.existsByName(categoryToDelete)).thenReturn(true);
        doNothing().when(categoryRepository).deleteByName(categoryToDelete);

        String expected = categoryService.deleteCategory(categoryToDelete);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testDeleteCategoryShouldThrowCategoryNotFoundException() {
        // Given
        String categoryToDelete = "category not found";

        // When
        when(categoryRepository.existsByName(categoryToDelete)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> categoryService.deleteCategory(categoryToDelete))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category '" + categoryToDelete + "' not found!");
    }

    @Test
    void testAddCategoryToPostShouldReturnString() {
        // Given
        String postToAddCategory = "Test";
        post.setPostCategories(new ArrayList<>());

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postToAddCategory, user)).thenReturn(true);
        when(categoryRepository.existsByName(categoryDto.getName())).thenReturn(true);
        when(postRepository.findByTitleAndUser(postToAddCategory, user)).thenReturn(post);
        when(categoryRepository.findByName(categoryDto.getName())).thenReturn(category);
        when(postCategoryRepository.existsByPostAndCategory(post, category)).thenReturn(false);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        String expected = categoryService.addCategoryToPost(postToAddCategory, categoryDto, TOKEN);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testAddCategoryToPostShouldThrowPostNotFoundException() {
        // Given
        String postToAddCategory = "Test";

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postToAddCategory, user)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> categoryService.addCategoryToPost(postToAddCategory, categoryDto, TOKEN))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with the title '" + postToAddCategory + "' not found!");
    }

    @Test
    void testAddCategoryToPostShouldThrowCategoryNotFoundException() {
        // Given
        String postToAddCategory = "Test";

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postToAddCategory, user)).thenReturn(true);
        when(categoryRepository.existsByName(categoryDto.getName())).thenReturn(false);

        // Then
        assertThatThrownBy(() -> categoryService.addCategoryToPost(postToAddCategory, categoryDto, TOKEN))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category '" + categoryDto.getName() + "' not found!");
    }

    @Test
    void testAddCategoryToPostShouldThrowPostAlreadyContainsThatCategoryException() {
        // Given
        String postToAddCategory = "Test";

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postToAddCategory, user)).thenReturn(true);
        when(categoryRepository.existsByName(categoryDto.getName())).thenReturn(true);
        when(postRepository.findByTitleAndUser(postToAddCategory, user)).thenReturn(post);
        when(categoryRepository.findByName(categoryDto.getName())).thenReturn(category);
        when(postCategoryRepository.existsByPostAndCategory(post, category)).thenReturn(true);

        // Then
        assertThatThrownBy(() -> categoryService.addCategoryToPost(postToAddCategory, categoryDto, TOKEN))
                .isInstanceOf(PostAlreadyContainsThatCategoryException.class)
                .hasMessageContaining(
                        "Post '" + postToAddCategory +
                                "' already contains category '" + categoryDto.getName() + "'"
                );
    }

    @Test
    void testDeleteCategoryFromPostShouldReturnString() {
        // Given
        String postDeleteCategory = "Test";
        post.setPostCategories(new ArrayList<>());

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postDeleteCategory, user)).thenReturn(true);
        when(categoryRepository.existsByName(categoryDto.getName())).thenReturn(true);
        when(postRepository.findByTitleAndUser(postDeleteCategory, user)).thenReturn(post);
        when(categoryRepository.findByName(categoryDto.getName())).thenReturn(category);
        when(postCategoryRepository.existsByPostAndCategory(post, category)).thenReturn(true);
        when(postCategoryRepository.findByPostAndCategory(post, category)).thenReturn(postCategory);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        String expected = categoryService.deleteCategoryFromPost(postDeleteCategory, categoryDto, TOKEN);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testDeleteCategoryFromPostShouldThrowPostNotFoundException() {
        // Given
        String postDeleteCategory = "Test";

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postDeleteCategory, user)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> categoryService.deleteCategoryFromPost(postDeleteCategory, categoryDto, TOKEN))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with the title '" + postDeleteCategory + "' not found!");
    }

    @Test
    void testDeleteCategoryFromPostThrowCategoryNotFoundException() {
        // Given
        String postDeleteCategory = "Test";

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postDeleteCategory, user)).thenReturn(true);
        when(categoryRepository.existsByName(categoryDto.getName())).thenReturn(false);

        // Then
        assertThatThrownBy(() -> categoryService.deleteCategoryFromPost(postDeleteCategory, categoryDto, TOKEN))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category '" + categoryDto.getName() + "' not found!");
    }

    @Test
    void testDeleteCategoryFromPostThrowPostDoesNotContainThatCategoryException() {
        // Given
        String postDeleteCategory = "Test";
        post.setPostCategories(new ArrayList<>());

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postDeleteCategory, user)).thenReturn(true);
        when(categoryRepository.existsByName(categoryDto.getName())).thenReturn(true);
        when(postRepository.findByTitleAndUser(postDeleteCategory, user)).thenReturn(post);
        when(categoryRepository.findByName(categoryDto.getName())).thenReturn(category);
        when(postCategoryRepository.existsByPostAndCategory(post, category)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> categoryService.deleteCategoryFromPost(postDeleteCategory, categoryDto, TOKEN))
                .isInstanceOf(PostDoesNotContainThatCategoryException.class)
                .hasMessageContaining(
                        "Post '" + postDeleteCategory +
                                "' does not contain category '" + categoryDto.getName() + "'"
                );
    }
}