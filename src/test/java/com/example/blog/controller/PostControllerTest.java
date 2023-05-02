package com.example.blog.controller;

import com.example.blog.dto.PostRequest;
import com.example.blog.dto.PostResponse;
import com.example.blog.dto.PostUpdateRequest;
import com.example.blog.security.JwtService;
import com.example.blog.service.PostService;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private JwtService jwtService;

    private PostResponse postResponse;
    private String postTitle;
    private final String jwtToken = "token";
    private static final String END_POINT_PATH = "/api/v1/posts";

    @BeforeEach
    void setUp() {
        postResponse = PostResponse.builder()
                .title("Test")
                .content("test")
                .categories(List.of("category"))
                .tags(List.of("tag"))
                .createdAt(LocalDateTime.now().withNano(0))
                .updatedAt(LocalDateTime.now().withNano(0))
                .build();

        postTitle = "Test";
    }

    @Test
    void createPost() throws Exception {
        // Given
        PostRequest postRequest = PostRequest.builder()
                .title("Test")
                .content("test")
                .categories(Set.of("category"))
                .tags(Set.of("tag"))
                .build();

        // When
        when(postService.createPost(postRequest, jwtToken)).thenReturn(postResponse);

        // Then
        mockMvc.perform(post(END_POINT_PATH + "/create")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(postResponse.getTitle()))
                .andExpect(jsonPath("$.content").value(postResponse.getContent()))
                .andExpect(jsonPath("$.categories[0]").value(postResponse.getCategories().get(0)))
                .andExpect(jsonPath("$.tags[0]").value(postResponse.getTags().get(0)))
                .andExpect(jsonPath("$.createdAt").value(postResponse.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updatedAt").value(postResponse.getUpdatedAt().toString()));
    }

    @Test
    void getPost() throws Exception {
        // When
        when(postService.getPost(postTitle)).thenReturn(postResponse);

        // Then
        mockMvc.perform(get(END_POINT_PATH + "/{postTitle}", postTitle))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(postResponse.getTitle()))
                .andExpect(jsonPath("$.content").value(postResponse.getContent()))
                .andExpect(jsonPath("$.categories[0]").value(postResponse.getCategories().get(0)))
                .andExpect(jsonPath("$.tags[0]").value(postResponse.getTags().get(0)))
                .andExpect(jsonPath("$.createdAt").value(postResponse.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updatedAt").value(postResponse.getUpdatedAt().toString()));
    }

    @Test
    void getAllPosts() throws Exception {
        // Given
        List<PostResponse> expectedPostResponseList = Collections.singletonList(postResponse);

        // When
        when(postService.getAllPosts()).thenReturn(List.of(postResponse));

        // Then
        MvcResult result = mockMvc.perform(get(END_POINT_PATH + "/all"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        List<PostResponse> actualPostResponseList = objectMapper.readValue(
                responseJson, new TypeReference<>() {
                }
        );
        assertThat(expectedPostResponseList).isEqualTo(actualPostResponseList);
    }

    @Test
    void updatePost() throws Exception {
        // Given
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("New Title")
                .content("New Content")
                .build();

        postResponse.setTitle(updateRequest.getTitle());
        postResponse.setContent(updateRequest.getContent());

        // When
        when(postService.updatePost(postTitle, updateRequest, jwtToken)).thenReturn(postResponse);

        // Then
        mockMvc.perform(put(END_POINT_PATH + "/update/{postTitle}", postTitle)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(jsonPath("$.title").value(postResponse.getTitle()))
                .andExpect(jsonPath("$.content").value(postResponse.getContent()))
                .andExpect(jsonPath("$.categories[0]").value(postResponse.getCategories().get(0)))
                .andExpect(jsonPath("$.tags[0]").value(postResponse.getTags().get(0)))
                .andExpect(jsonPath("$.createdAt").value(postResponse.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updatedAt").value(postResponse.getUpdatedAt().toString()));
    }

    @Test
    void deletePost() throws Exception {
        // Given
        String response = "Post with the title '" + postTitle + "' deleted";

        // When
        when(postService.deletePost(postTitle, jwtToken)).thenReturn(response);

        // Then
        mockMvc.perform(delete(END_POINT_PATH + "/{postTitle}", postTitle)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    void getAllPostsByCategory() throws Exception {
        // Given
        String categoryName = "category";
        List<PostResponse> expectedPostResponseList = Collections.singletonList(postResponse);

        // When
        when(postService.getAllPostsByCategory(categoryName)).thenReturn(List.of(postResponse));

        // Then
        MvcResult result = mockMvc.perform(get(END_POINT_PATH + "/all-by-category")
                .param("categoryName", categoryName))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        List<PostResponse> actualPostResponseList = objectMapper.readValue(
                responseJson, new TypeReference<>() {
                }
        );
        assertThat(expectedPostResponseList).isEqualTo(actualPostResponseList);
    }

    @Test
    void getAllPostsByTag() throws Exception {
        // Given
        String tagName = "tag";
        List<PostResponse> expectedPostResponseList = Collections.singletonList(postResponse);

        // When
        when(postService.getAllPostsByTag(tagName)).thenReturn(List.of(postResponse));

        // Then
        MvcResult result = mockMvc.perform(get(END_POINT_PATH + "/all-by-tag")
                        .param("tagName", tagName))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        List<PostResponse> actualPostResponseList = objectMapper.readValue(
                responseJson, new TypeReference<>() {
                }
        );
        assertThat(expectedPostResponseList).isEqualTo(actualPostResponseList);
    }
}