package com.vanguard.authservice.exception;

public class UserAlreadyExistsException extends IllegalArgumentException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
