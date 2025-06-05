package com.userService.exception;

public class UserCodeExistsException extends RuntimeException {
    public UserCodeExistsException(String message) {
        super(message);
    }
}
