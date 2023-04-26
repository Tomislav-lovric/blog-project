package com.example.blog.repository;

import com.example.blog.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PostCategoryRepositoryTest {

    @Autowired
    private PostCategoryRepository postCategoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Post post;
    private Category category;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .firstName("John")
                .lastName("Evans")
                .email("john_evans@gmail.com")
                .password("Test.123")
                .repeatPassword("Test.123")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        post = Post.builder()
                .title("Test")
                .content("test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();
        postRepository.save(post);

        category = Category.builder().name("category").build();
        categoryRepository.save(category);

        PostCategory postCategory = PostCategory.builder()
                .postCategoryId(new PostCategoryId(post.getId(), category.getId()))
                .post(post)
                .category(category)
                .build();
        postCategoryRepository.save(postCategory);
    }

    @Test
    void testExistsByPostAndCategoryShouldReturnTrue() {
        // When
        boolean expected = postCategoryRepository.existsByPostAndCategory(post, category);

        // Then
        assertThat(expected).isTrue();
    }

    @Test
    void testExistsByPostAndCategoryShouldReturnFalse() {
        // Given
        Category categoryToFail = Category.builder().name("Fail").build();
        categoryRepository.save(categoryToFail);

        // When
        boolean expected = postCategoryRepository.existsByPostAndCategory(post, categoryToFail);

        // Then
        assertThat(expected).isFalse();
    }

    @Test
    void testFindByPostAndCategoryShouldReturnPostCategory() {
        // When
        PostCategory expected = postCategoryRepository.findByPostAndCategory(post, category);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testFindByPostShouldReturnPostCategoryList() {
        // When
        List<PostCategory> expected = postCategoryRepository.findByPost(post);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testFindByCategoryShouldReturnPostCategoryList() {
        // When
        List<PostCategory> expected = postCategoryRepository.findByCategory(category);

        // Then
        assertThat(expected).isNotNull();
    }
}