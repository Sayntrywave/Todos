package com.korotkov.todo.util.errorResponse;

import lombok.Data;

@Data
public class ErrorResponse {
    private int code;
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }


}