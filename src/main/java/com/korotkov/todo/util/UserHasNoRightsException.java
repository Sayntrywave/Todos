package com.korotkov.todo.util;

public class UserHasNoRightsException extends RuntimeException{
    public UserHasNoRightsException(String message) {
        super(message);
    }
}
