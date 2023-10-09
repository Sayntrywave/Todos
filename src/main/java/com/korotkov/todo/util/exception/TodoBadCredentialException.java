package com.korotkov.todo.util.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class TodoBadCredentialException extends BadCredentialsException {
    public TodoBadCredentialException(String msg) {
        super(msg);
    }
}
