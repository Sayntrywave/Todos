package com.korotkov.todo.util.exception;

public class TodoNotFoundException extends RuntimeException{
    public TodoNotFoundException() {
        super("todo not found");
    }
}
