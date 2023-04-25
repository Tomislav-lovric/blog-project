package com.example.blog.service;

import com.example.blog.dto.CommentRequest;
import com.example.blog.dto.CommentResponse;
import com.example.blog.entity.Comment;
import com.example.blog.exception.CommentNotFoundException;
import com.example.blog.exception.PostNotFoundException;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    //Methods are not complicated, I think, so no need for comments for most part
    public CommentResponse createComment(String postTitle, CommentRequest request, String bearerToken) {
        String username = jwtService.extractUsername(bearerToken.substring(7));
        var user = userRepository.findUserByEmail(username);

        if (!postRepository.existsByTitle(postTitle)) {
            throw new PostNotFoundException("Post with the title '" + postTitle + "' not found!");
        }

        var post = postRepository.findByTitle(postTitle);
        var comment = Comment.builder()
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .post(post)
                .build();
        commentRepository.save(comment);

        return CommentResponse.builder()
                .userName(username)
                .content(request.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public CommentResponse getComment(String postTitle, Long commentId) {
        if (!postRepository.existsByTitle(postTitle)) {
            throw new PostNotFoundException("Post with the title '" + postTitle + "' not found!");
        }
        var post = postRepository.findByTitle(postTitle);

        if (!commentRepository.existsByIdAndPost(commentId, post)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        var comment = commentRepository.findByIdAndPost(commentId, post);

        return CommentResponse.builder()
                .userName(comment.getUser().getEmail())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public List<CommentResponse> getAllComments(String postTitle) {
        if (!postRepository.existsByTitle(postTitle)) {
            throw new PostNotFoundException("Post with the title '" + postTitle + "' not found!");
        }

        var post = postRepository.findByTitle(postTitle);
        var comments = commentRepository.findAllByPost(post);

        return comments.stream().map(comment -> CommentResponse.builder()
                .userName(comment.getUser().getEmail())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build()).toList();
    }

    @Transactional
    public CommentResponse updateComment(String postTitle, Long commentId, CommentRequest request, String bearerToken) {
        String username = jwtService.extractUsername(bearerToken.substring(7));
        var user = userRepository.findUserByEmail(username);

        if (!postRepository.existsByTitle(postTitle)) {
            throw new PostNotFoundException("Post with the title '" + postTitle + "' not found!");
        }
        var post = postRepository.findByTitle(postTitle);

        //I think only this code kinda needs an explanation.
        //We are checking if comment exists on post with provided title, by id provided
        //that was made by the user trying to edit it now
        if (!commentRepository.existsByIdAndPostAndUser(commentId, post, user)) {
            throw new CommentNotFoundException("Comment not found");
        }
        var comment = commentRepository.findByIdAndPostAndUser(commentId, post, user);

        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        return CommentResponse.builder()
                .userName(comment.getUser().getEmail())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    @Transactional
    public String deleteComment(String postTitle, Long commentId, String bearerToken) {
        String username = jwtService.extractUsername(bearerToken.substring(7));
        var user = userRepository.findUserByEmail(username);

        if (!postRepository.existsByTitle(postTitle)) {
            throw new PostNotFoundException("Post with the title '" + postTitle + "' not found!");
        }
        var post = postRepository.findByTitle(postTitle);

        //We are checking if comment exists on post with provided title, by id provided
        //that was made by the user trying to delete it now
        if (!commentRepository.existsByIdAndPostAndUser(commentId, post, user)) {
            throw new CommentNotFoundException("Comment not found");
        }

        commentRepository.deleteById(commentId);

        return "Comment deleted!";
    }
}
