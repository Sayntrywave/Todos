package com.korotkov.todo.util.exception;

public class UserHasNoRightsException extends RuntimeException {
    public UserHasNoRightsException(String message) {
        super(message);
    }
}
