package com.example.blog.repository;

import com.example.blog.entity.Post;
import com.example.blog.entity.Role;
import com.example.blog.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private Post post;
    private User user;

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
        userRepository.save(user);

        post = Post.builder()
                .title("Test")
                .content("test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();
        postRepository.save(post);
    }

    @Test
    void testExistsByTitleAndUserShouldReturnTrue() {
        // When
        boolean expected = postRepository.existsByTitleAndUser(post.getTitle(), user);

        // Then
        assertThat(expected).isTrue();
    }

    @Test
    void testExistsByTitleAndUserShouldReturnFalse() {
        // Given
        String titleToFail = "Fail";

        // When
        boolean expected = postRepository.existsByTitleAndUser(titleToFail, user);

        // Then
        assertThat(expected).isFalse();
    }

    @Test
    void testFindByTitleAndUserShouldReturnPost() {
        // When
        Post expected = postRepository.findByTitleAndUser(post.getTitle(), user);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testExistsByTitleShouldReturnTrue() {
        // When
        boolean expected = postRepository.existsByTitle(post.getTitle());

        // Then
        assertThat(expected).isTrue();
    }

    @Test
    void testExistsByTitleShouldReturnFalse() {
        // Given
        String titleToFail = "Fail";

        // When
        boolean expected = postRepository.existsByTitle(titleToFail);

        // Then
        assertThat(expected).isFalse();
    }

    @Test
    void testFindByTitleShouldReturnPost() {
        // When
        Post expected = postRepository.findByTitle(post.getTitle());

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testDeleteByTitleAndUserShouldReturnVoid() {
        // Given
        postRepository.deleteByTitleAndUser(post.getTitle(), user);

        // When
        Optional<Post> expected = postRepository.findById(post.getId());

        // Then
        assertThat(expected).isEmpty();
    }
}