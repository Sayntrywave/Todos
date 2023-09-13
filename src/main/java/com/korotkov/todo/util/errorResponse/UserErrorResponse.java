package com.korotkov.todo.util.errorResponse;

public class UserErrorResponse extends ErrorResponse {
    public UserErrorResponse(String message) {
        super(1, message);
    }
}
