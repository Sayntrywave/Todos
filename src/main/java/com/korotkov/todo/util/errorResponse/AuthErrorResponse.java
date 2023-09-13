package com.korotkov.todo.util.errorResponse;

public class AuthErrorResponse extends ErrorResponse {
    public AuthErrorResponse(String message) {
        super(1, message);
    }
}
