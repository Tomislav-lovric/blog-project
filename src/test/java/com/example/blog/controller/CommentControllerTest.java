package com.example.blog.controller;

import com.example.blog.dto.CommentRequest;
import com.example.blog.dto.CommentResponse;
import com.example.blog.security.JwtService;
import com.example.blog.service.CommentService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JwtService jwtService;

    private CommentRequest commentRequest;
    private CommentResponse commentResponse;
    private Long commentId;
    private String postTitle;
    private final String jwtToken = "token";
    private static final String END_POINT_PATH = "/api/v1/posts";

    @BeforeEach
    void setUp() {
        commentRequest = CommentRequest.builder().content("Content").build();

        commentResponse = CommentResponse.builder()
                .userName("john_evans@gmail.com")
                .content("Content")
                .createdAt(LocalDateTime.now().withNano(0))
                .updatedAt(LocalDateTime.now().withNano(0))
                .build();

        commentId = 1L;

        postTitle = "Test";
    }

    @Test
    void createComment() throws Exception {
        // When
        when(commentService.createComment(postTitle, commentRequest, jwtToken)).thenReturn(commentResponse);

        // Then
        mockMvc.perform(post(END_POINT_PATH + "/{postTitle}/comments/create", postTitle)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value(commentResponse.getUserName()))
                .andExpect(jsonPath("$.content").value(commentResponse.getContent()))
                .andExpect(jsonPath("$.createdAt").value(commentResponse.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updatedAt").value(commentResponse.getUpdatedAt().toString()));
    }

    @Test
    void getComment() throws Exception {
        // When
        when(commentService.getComment(postTitle, commentId)).thenReturn(commentResponse);

        // Then
        mockMvc.perform(get(END_POINT_PATH + "/{postTitle}/comments/{commentId}", postTitle, commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(commentResponse.getUserName()))
                .andExpect(jsonPath("$.content").value(commentResponse.getContent()))
                .andExpect(jsonPath("$.createdAt").value(commentResponse.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updatedAt").value(commentResponse.getUpdatedAt().toString()));
    }

    @Test
    void getAllComments() throws Exception {
        // Given
        List<CommentResponse> expectedCommentResponseList = Collections.singletonList(commentResponse);

        // When
        when(commentService.getAllComments(postTitle)).thenReturn(List.of(commentResponse));

        // Then
        MvcResult result = mockMvc.perform(get(END_POINT_PATH + "/{postTitle}/comments/all", postTitle))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        List<CommentResponse> actualCommentResponseList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });
        assertThat(expectedCommentResponseList).isEqualTo(actualCommentResponseList);
    }

    @Test
    void updateComment() throws Exception {
        // Given
        commentRequest.setContent("New Content");

        // When
        when(commentService.updateComment(postTitle, commentId, commentRequest, jwtToken)).thenReturn(commentResponse);

        // Then
        mockMvc.perform(put(END_POINT_PATH + "/{postTitle}/comments/{commentId}/update", postTitle, commentId)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(commentResponse.getUserName()))
                .andExpect(jsonPath("$.content").value(commentResponse.getContent()))
                .andExpect(jsonPath("$.createdAt").value(commentResponse.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updatedAt").value(commentResponse.getUpdatedAt().toString()));
    }

    @Test
    void deleteComment() throws Exception {
        // Given
        String response = "comment deleted";

        // When
        when(commentService.deleteComment(postTitle, commentId, jwtToken)).thenReturn(response);

        // Then
        mockMvc.perform(delete(END_POINT_PATH + "/{postTitle}/comments/{commentId}/delete", postTitle, commentId)
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }
}