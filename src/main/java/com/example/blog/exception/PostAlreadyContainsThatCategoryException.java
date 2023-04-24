package com.example.blog.exception;

public class PostAlreadyContainsThatCategoryException extends RuntimeException {
    private String message;

    public PostAlreadyContainsThatCategoryException() {
    }

    public PostAlreadyContainsThatCategoryException(String message) {
        super(message);
        this.message = message;
    }
}
