package com.example.blog.exception;

public class InvalidRepeatedPasswordException extends RuntimeException {
    private String message;

    public InvalidRepeatedPasswordException() {
    }

    public InvalidRepeatedPasswordException(String message) {
        super(message);
        this.message = message;
    }
}
