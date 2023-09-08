package com.korotkov.todo.util.errorResponse;

import com.korotkov.todo.util.errorResponse.ErrorResponse;


public class TodoErrorResponse extends ErrorResponse {
    public TodoErrorResponse(String message) {
        super(0,message);
    }
}
