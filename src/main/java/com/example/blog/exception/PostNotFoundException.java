package com.example.blog.exception;

public class PostNotFoundException extends RuntimeException {
    private String message;

    public PostNotFoundException() {
    }

    public PostNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
