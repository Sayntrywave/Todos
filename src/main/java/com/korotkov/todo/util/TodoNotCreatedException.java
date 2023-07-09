package com.korotkov.todo.util;

public class TodoNotCreatedException extends RuntimeException{

    public TodoNotCreatedException(String message) {
        super(message);
    }
}
