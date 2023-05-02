package com.example.blog.controller;

import com.example.blog.dto.CategoryDto;
import com.example.blog.security.JwtService;
import com.example.blog.service.CategoryService;
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

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtService jwtService;

    private CategoryDto categoryDto;
    private String categoryName;
    private String postTitle;
    private final String jwtToken = "token";
    private static final String END_POINT_PATH = "/api/v1/posts";

    @BeforeEach
    void setUp() {
        categoryDto = CategoryDto.builder().name("category").build();

        categoryName = "category";

        postTitle = "Test";
    }

    @Test
    void createCategory() throws Exception {
        // When
        when(categoryService.createCategory(categoryDto)).thenReturn(categoryDto);

        // Then
        mockMvc.perform(post(END_POINT_PATH + "/categories/create")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(categoryDto.getName()));
    }

    @Test
    void createCategories() throws Exception {
        // Given
        CategoryDto categoryDto2 = CategoryDto.builder().name("category2").build();
        List<CategoryDto> expectedCategoryDtoList = Arrays.asList(categoryDto, categoryDto2);

        // When
        when(categoryService.createCategory(any(CategoryDto.class))).thenReturn(categoryDto, categoryDto2);

        // Then
        MvcResult result = mockMvc.perform(post(END_POINT_PATH + "/categories/create-multi")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(categoryDto, categoryDto2))))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        List<CategoryDto> actualCategoryDtoList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });
        assertThat(expectedCategoryDtoList).isEqualTo(actualCategoryDtoList);
    }

    @Test
    void searchCategory() throws Exception {
        // Given
        String category = "category";
        String response = "category found";

        // When
        when(categoryService.searchCategory(category)).thenReturn(response);

        // Then
        mockMvc.perform(get(END_POINT_PATH + "/categories/search")
                .param("category", category))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    void getAllCategories() throws Exception {
        // Given
        List<CategoryDto> expectedCategoryDtoList = Collections.singletonList(categoryDto);

        // When
        when(categoryService.getAllCategories()).thenReturn(List.of(categoryDto));

        // Then
        MvcResult result = mockMvc.perform(get(END_POINT_PATH + "/categories/all"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        List<CategoryDto> actualCategoryDtoList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });
        assertThat(expectedCategoryDtoList).isEqualTo(actualCategoryDtoList);
    }

    @Test
    void updateCategory() throws Exception {
        // Given
        String response = "Category updated";

        // When
        when(categoryService.updateCategory(categoryName, categoryDto)).thenReturn(response);

        // Then
        mockMvc.perform(put(END_POINT_PATH + "/categories/update/{categoryName}", categoryName)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    void deleteCategory() throws Exception {
        // Given
        String response = "Category deleted";

        // When
        when(categoryService.deleteCategory(categoryName)).thenReturn(response);

        // Then
        mockMvc.perform(delete(END_POINT_PATH + "/categories/delete/{categoryName}", categoryName)
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    void addCategoryToPost() throws Exception {
        // Given
        String response = "Category added";

        // When
        when(categoryService.addCategoryToPost(postTitle, categoryDto, jwtToken)).thenReturn(response);

        // Then
        mockMvc.perform(put(END_POINT_PATH + "/{postTitle}/categories/add", postTitle)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    void addCategoriesToPost() throws Exception {
        // Given
        String response = "All categories added to the post '" + postTitle + "'";

        // When
        when(categoryService.addCategoryToPost(postTitle, categoryDto, jwtToken)).thenReturn(response);

        // Then
        mockMvc.perform(put(END_POINT_PATH + "/{postTitle}/categories/add-multi", postTitle)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(categoryDto))))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    void deleteCategoriesFromPost() throws Exception {
        // Given
        String response = "Category deleted from the post";

        // When
        when(categoryService.deleteCategoryFromPost(postTitle, categoryDto, jwtToken)).thenReturn(response);

        // Then
        mockMvc.perform(delete(END_POINT_PATH + "/{postTitle}/categories/delete", postTitle)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }
}