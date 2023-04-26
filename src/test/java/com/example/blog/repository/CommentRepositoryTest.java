package com.example.blog.repository;

import com.example.blog.entity.Comment;
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
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private Comment comment;
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

        comment = Comment.builder()
                .content("Comments content")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .post(post)
                .build();
        commentRepository.save(comment);
    }

    @Test
    void testExistsByIdAndPostShouldReturnTrue() {
        // When
        boolean expected = commentRepository.existsByIdAndPost(comment.getId(), post);

        // Then
        assertThat(expected).isTrue();
    }

    @Test
    void testExistsByIdAndPostShouldReturnFalse() {
        // Given
        Long idToFail = 10L;

        // When
        boolean expected = commentRepository.existsByIdAndPost(idToFail, post);

        // Then
        assertThat(expected).isFalse();
    }

    @Test
    void testFindByIdAndPostShouldReturnComment() {
        // When
        Comment expected = commentRepository.findByIdAndPost(comment.getId(), post);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testFindAllByPostShouldReturnCommentList() {
        // When
        List<Comment> expected = commentRepository.findAllByPost(post);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testExistsByIdAndPostAndUserShouldReturnTrue() {
        // When
        boolean expected = commentRepository.existsByIdAndPostAndUser(comment.getId(), post, user);

        // Then
        assertThat(expected).isTrue();
    }

    @Test
    void testExistsByIdAndPostAndUserShouldReturnFalse() {
        // Given
        Long idToFail = 10L;

        // When
        boolean expected = commentRepository.existsByIdAndPostAndUser(idToFail, post, user);

        // Then
        assertThat(expected).isFalse();
    }

    @Test
    void testFindByIdAndPostAndUserShouldReturnComment() {
        // When
        Comment expected = commentRepository.findByIdAndPostAndUser(comment.getId(), post, user);

        // Then
        assertThat(expected).isNotNull();
    }
}