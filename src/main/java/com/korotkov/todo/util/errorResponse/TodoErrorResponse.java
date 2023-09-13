package com.korotkov.todo.util.errorResponse;

public class TodoErrorResponse extends ErrorResponse {
    public TodoErrorResponse(String message) {
        super(1, message);
    }
}
