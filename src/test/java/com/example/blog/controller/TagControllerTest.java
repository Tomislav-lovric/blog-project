package com.example.blog.controller;

import com.example.blog.dto.TagDto;
import com.example.blog.security.JwtService;
import com.example.blog.service.TagService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false)
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TagService tagService;

    @MockBean
    private JwtService jwtService;

    private TagDto tagDto;
    private String tagName;
    private String postTitle;
    private final String jwtToken = "token";
    private static final String END_POINT_PATH = "/api/v1/posts";

    @BeforeEach
    void setUp() {
        tagDto = TagDto.builder().name("tag").build();

        tagName = "tag";

        postTitle = "Title";
    }

    @Test
    void createTag() throws Exception {
        // When
        when(tagService.createTag(tagDto)).thenReturn(tagDto);

        // Then
        mockMvc.perform(post(END_POINT_PATH + "/tags/create")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(tagDto.getName()));
    }

    @Test
    void createTags() throws Exception {
        // Given
        TagDto tagDto2 = TagDto.builder().name("tag2").build();
        List<TagDto> expectedTagDtoList = Arrays.asList(tagDto, tagDto2);

        // When
        when(tagService.createTag(any(TagDto.class))).thenReturn(tagDto, tagDto2);

        // Then
        MvcResult result = mockMvc.perform(post(END_POINT_PATH + "/tags/create-multi")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(tagDto, tagDto2))))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        List<TagDto> actualTagDtoList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });
        assertThat(expectedTagDtoList).isEqualTo(actualTagDtoList);
    }

    @Test
    void searchTag() throws Exception {
        // Given
        String tag = "tag";
        String response = "tag found";

        // When
        when(tagService.searchTag(tag)).thenReturn(response);

        // Then
        mockMvc.perform(get(END_POINT_PATH + "/tags/search")
                .param("tag", tag))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    void getAllTags() throws Exception {
        // Given
        List<TagDto> expectedTagDtoList = Collections.singletonList(tagDto);

        // When
        when(tagService.getAllTags()).thenReturn(List.of(tagDto));

        // Then
        MvcResult result = mockMvc.perform(get(END_POINT_PATH + "/tags/all"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        List<TagDto> actualTagDtoList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });
        assertThat(expectedTagDtoList).isEqualTo(actualTagDtoList);
    }

    @Test
    void updateTag() throws Exception {
        // Given
        String response = "tag updated";

        // When
        when(tagService.updateTag(tagName, tagDto)).thenReturn(response);

        // Then
        mockMvc.perform(put(END_POINT_PATH + "/tags/update/{tagName}", tagName)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    void deleteTag() throws Exception {
        // Given
        String response = "tag deleted";

        // When
        when(tagService.deleteTag(tagName)).thenReturn(response);

        // Then
        mockMvc.perform(delete(END_POINT_PATH + "/tags/delete/{tagName}", tagName)
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    void addTagToPost() throws Exception {
        // Given
        String response = "tag added";

        // When
        when(tagService.addTagToPost(postTitle, tagDto, jwtToken)).thenReturn(response);

        // Then
        mockMvc.perform(put(END_POINT_PATH + "/{postTitle}/tags/add", postTitle)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    void addTagsToPost() throws Exception {
        // Given
        String response = "All tags added to the post '" + postTitle + "'";

        // When
        when(tagService.addTagToPost(postTitle, tagDto, jwtToken)).thenReturn(response);

        // Then
        mockMvc.perform(put(END_POINT_PATH + "/{postTitle}/tags/add-multi", postTitle)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(tagDto))))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    void deleteTagFromPost() throws Exception {
        // Given
        String response = "Category deleted from the post";

        // When
        when(tagService.deleteTagFromPost(postTitle, tagDto, jwtToken)).thenReturn(response);

        // Then
        mockMvc.perform(delete(END_POINT_PATH + "/{postTitle}/tags/delete", postTitle)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }
}