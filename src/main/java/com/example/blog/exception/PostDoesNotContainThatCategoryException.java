package com.example.blog.exception;

public class PostDoesNotContainThatCategoryException extends RuntimeException {
    private String message;

    public PostDoesNotContainThatCategoryException() {
    }

    public PostDoesNotContainThatCategoryException(String message) {
        super(message);
        this.message = message;
    }
}
