package com.korotkov.todo.util.errorResponse;

import com.korotkov.todo.util.errorResponse.ErrorResponse;

public class UserErrorResponse extends ErrorResponse {
    public UserErrorResponse(String message) {
        super(0,message);
    }
}
