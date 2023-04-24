package com.example.blog.exception;

public class TagAlreadyExistsException extends RuntimeException {
    private String message;

    public TagAlreadyExistsException() {
    }

    public TagAlreadyExistsException(String message) {
        super(message);
        this.message = message;
    }
}
