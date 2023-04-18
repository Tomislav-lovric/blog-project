package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    @NotBlank(message = "Title should not be blank")
    private String title;

    @NotBlank(message = "Content should not be blank")
    private String content;

}
