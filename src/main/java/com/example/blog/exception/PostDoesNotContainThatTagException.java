package com.example.blog.exception;

public class PostDoesNotContainThatTagException extends RuntimeException {
    private String message;

    public PostDoesNotContainThatTagException() {
    }

    public PostDoesNotContainThatTagException(String message) {
        super(message);
        this.message = message;
    }
}
