package com.example.blog.exception;

public class PostAlreadyContainsThatTagException extends RuntimeException {
    private String message;

    public PostAlreadyContainsThatTagException() {
    }

    public PostAlreadyContainsThatTagException(String message) {
        super(message);
        this.message = message;
    }
}
