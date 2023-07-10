package com.korotkov.todo.util;

import lombok.Data;

@Data
public class TodoErrorResponse {
    private int code = 0;
    private String message;

    public TodoErrorResponse(String message) {
        this.message = message;
    }

    public TodoErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }


}
