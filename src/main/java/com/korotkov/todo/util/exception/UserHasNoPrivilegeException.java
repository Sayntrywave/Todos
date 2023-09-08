package com.korotkov.todo.util.exception;

public class UserHasNoPrivilegeException extends RuntimeException{
    public UserHasNoPrivilegeException(String message) {
        super(message);
    }
}
