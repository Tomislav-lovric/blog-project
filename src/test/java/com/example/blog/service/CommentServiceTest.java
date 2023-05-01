package com.example.blog.service;

import com.example.blog.dto.CommentRequest;
import com.example.blog.dto.CommentResponse;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.Role;
import com.example.blog.entity.User;
import com.example.blog.exception.CommentNotFoundException;
import com.example.blog.exception.PostNotFoundException;
import com.example.blog.repository.CommentRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    private static final String TOKEN = "bearer token";

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private CommentRequest commentRequest;
    private Post post;
    private User user;
    private String postTitle;

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

        comment = Comment.builder()
                .content("Comments content")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .post(post)
                .build();

        commentRequest = CommentRequest.builder().content("Comments content").build();

        postTitle = "Test";
    }

    @Test
    void testCreateCommentShouldReturnCommentResponse() {
        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitle(postTitle)).thenReturn(true);
        when(postRepository.findByTitle(postTitle)).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponse expected = commentService.createComment(postTitle, commentRequest, TOKEN);

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.getContent()).isEqualTo(commentRequest.getContent());
    }

    @Test
    void testCreateCommentShouldReturnThrowPostNotFoundException() {
        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitle(postTitle)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> commentService.createComment(postTitle, commentRequest, TOKEN))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with the title '" + postTitle + "' not found!");
    }

    @Test
    void testGetCommentShouldReturnCommentResponse() {
        // Given
        Long commentId = 1L;

        // When
        when(postRepository.existsByTitle(postTitle)).thenReturn(true);
        when(postRepository.findByTitle(postTitle)).thenReturn(post);
        when(commentRepository.existsByIdAndPost(commentId, post)).thenReturn(true);
        when(commentRepository.findByIdAndPost(commentId, post)).thenReturn(comment);

        CommentResponse expected = commentService.getComment(postTitle, commentId);

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.getContent()).isEqualTo(comment.getContent());
        assertThat(expected.getUserName()).isEqualTo(comment.getUser().getEmail());
        assertThat(expected.getCreatedAt()).isEqualTo(comment.getCreatedAt());
        assertThat(expected.getUpdatedAt()).isEqualTo(comment.getUpdatedAt());
    }

    @Test
    void testGetCommentShouldThrowPostNotFoundException() {
        // Given
        Long commentId = 1L;

        // When
        when(postRepository.existsByTitle(postTitle)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> commentService.getComment(postTitle, commentId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with the title '" + postTitle + "' not found!");
    }

    @Test
    void testGetCommentShouldThrowCommentNotFoundException() {
        // Given
        Long commentId = 1L;

        // When
        when(postRepository.existsByTitle(postTitle)).thenReturn(true);
        when(postRepository.findByTitle(postTitle)).thenReturn(post);
        when(commentRepository.existsByIdAndPost(commentId, post)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> commentService.getComment(postTitle, commentId))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("Comment not found!");
    }

    @Test
    void testGetAllCommentsShouldReturnCommentResponseList() {
        // When
        when(postRepository.existsByTitle(postTitle)).thenReturn(true);
        when(postRepository.findByTitle(postTitle)).thenReturn(post);
        when(commentRepository.findAllByPost(post)).thenReturn(List.of(comment));

        List<CommentResponse> expected = commentService.getAllComments(postTitle);

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.get(0).getContent()).isEqualTo(comment.getContent());
        assertThat(expected.get(0).getUserName()).isEqualTo(comment.getUser().getEmail());
        assertThat(expected.get(0).getCreatedAt()).isEqualTo(comment.getCreatedAt());
        assertThat(expected.get(0).getUpdatedAt()).isEqualTo(comment.getUpdatedAt());
    }

    @Test
    void testGetAllCommentsShouldThrowPostNotFoundException() {
        // When
        when(postRepository.existsByTitle(postTitle)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> commentService.getAllComments(postTitle))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with the title '" + postTitle + "' not found!");
    }

    @Test
    void testUpdateCommentShouldReturnCommentResponse() {
        // Given
        Long commentId = 1L;

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitle(postTitle)).thenReturn(true);
        when(postRepository.findByTitle(postTitle)).thenReturn(post);
        when(commentRepository.existsByIdAndPostAndUser(commentId, post, user)).thenReturn(true);
        when(commentRepository.findByIdAndPostAndUser(commentId, post, user)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponse expected = commentService.updateComment(postTitle, commentId, commentRequest, TOKEN);

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.getContent()).isEqualTo(comment.getContent());
        assertThat(expected.getUserName()).isEqualTo(comment.getUser().getEmail());
        assertThat(expected.getCreatedAt()).isEqualTo(comment.getCreatedAt());
        assertThat(expected.getUpdatedAt()).isEqualTo(comment.getUpdatedAt());
    }

    @Test
    void testUpdateCommentShouldThrowPostNotFoundException() {
        // Given
        Long commentId = 1L;

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitle(postTitle)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> commentService.updateComment(postTitle, commentId, commentRequest, TOKEN))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with the title '" + postTitle + "' not found!");
    }

    @Test
    void testUpdateCommentShouldThrowCommentNotFoundException() {
        // Given
        Long commentId = 1L;

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitle(postTitle)).thenReturn(true);
        when(postRepository.findByTitle(postTitle)).thenReturn(post);
        when(commentRepository.existsByIdAndPostAndUser(commentId, post, user)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> commentService.updateComment(postTitle, commentId, commentRequest, TOKEN))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("Comment not found");
    }

    @Test
    void testDeleteCommentShouldReturnString() {
        // Given
        Long commentId = 1L;

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitle(postTitle)).thenReturn(true);
        when(postRepository.findByTitle(postTitle)).thenReturn(post);
        when(commentRepository.existsByIdAndPostAndUser(commentId, post, user)).thenReturn(true);
        doNothing().when(commentRepository).deleteById(commentId);

        String expected = commentService.deleteComment(postTitle, commentId, TOKEN);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testDeleteCommentShouldThrowPostNotFoundException() {
        // Given
        Long commentId = 1L;

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitle(postTitle)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> commentService.deleteComment(postTitle, commentId, TOKEN))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with the title '" + postTitle + "' not found!");
    }

    @Test
    void testDeleteCommentShouldThrowCommentNotFoundException() {
        // Given
        Long commentId = 1L;

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitle(postTitle)).thenReturn(true);
        when(postRepository.findByTitle(postTitle)).thenReturn(post);
        when(commentRepository.existsByIdAndPostAndUser(commentId, post, user)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> commentService.deleteComment(postTitle, commentId, TOKEN))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("Comment not found");
    }
}