package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    @NotBlank(message = "Title should not be blank")
    private String title;

    @NotBlank(message = "Content should not be blank")
    private String content;

    //For these two categories we are using Set to make them unique
    //because we do not want posts to have 2 or more of the same
    //categories/tags
    @NotEmpty(message = "Categories should not be empty")
    private Set<String> categories;

    @NotEmpty(message = "Tags should not be empty")
    private Set<String> tags;

}
