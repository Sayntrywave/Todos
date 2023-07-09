package com.korotkov.todo.util;

import com.korotkov.todo.model.User;

public class UserErrorResponse {
    private int code = 0;
    private String message;

    public UserErrorResponse(String message) {
        this.message = message;
    }

    public UserErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
