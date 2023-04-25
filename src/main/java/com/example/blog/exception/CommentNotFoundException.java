package com.example.blog.exception;

public class CommentNotFoundException extends RuntimeException {
    private String message;

    public CommentNotFoundException() {
    }

    public CommentNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
