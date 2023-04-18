package com.example.blog.exception;

public class PostTitleAlreadyExistsException extends RuntimeException {
    private String message;

    public PostTitleAlreadyExistsException() {
    }

    public PostTitleAlreadyExistsException(String message) {
        super(message);
        this.message = message;
    }
}
