package com.korotkov.todo.controller.exceptionHandler;


import com.korotkov.todo.util.errorResponse.UserErrorResponse;
import com.korotkov.todo.util.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler
    private ResponseEntity<String> handleException(BadCredentialsException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotFoundException e) {
        UserErrorResponse userErrorResponse = new UserErrorResponse(e.getMessage());
        return new ResponseEntity<>(userErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
