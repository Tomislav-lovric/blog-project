package com.example.blog.exception;

public class TagNotFoundException extends RuntimeException {
    private String message;

    public TagNotFoundException() {
    }

    public TagNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
