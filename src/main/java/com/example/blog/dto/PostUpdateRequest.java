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
public class PostUpdateRequest {
    //Since user does not necessarily want to update both title and content
    //we are not using any validation like @NotBlank or similar
    private String title;
    private String content;

}
