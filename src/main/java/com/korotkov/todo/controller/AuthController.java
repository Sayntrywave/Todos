package com.korotkov.todo.controller;

import com.korotkov.todo.dto.request.AuthenticationRequest;
import com.korotkov.todo.model.User;
import com.korotkov.todo.security.JWTUtil;
import com.korotkov.todo.service.RegistrationService;
import com.korotkov.todo.util.TodoErrorResponse;
import com.korotkov.todo.util.UserNotCreatedException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = {"http://localhost:4000","https://localhost:4000"},allowCredentials = "true")
@RestController
public class AuthController {
    private final RegistrationService service;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;


    @Autowired
    public AuthController(RegistrationService service, AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.service = service;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/hello")
    public String printHello(){
        return "Hello";
    }

    // TODO на регистрацию добавить валидацию
    @PostMapping("/register")
    public HttpStatus registration(@RequestBody User user){
        service.register(user);
        return HttpStatus.OK;
    }
    @PostMapping("/login")
    public Map<String,String> login (@RequestBody @Valid AuthenticationRequest authenticationRequest, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new UserNotCreatedException(bindingResult.getFieldError().getField() + " is empty");
        }

        UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(
                authenticationRequest.getLogin(),
                authenticationRequest.getPassword()
        );

        try {
            authenticationManager.authenticate(authInputToken);
        } catch (BadCredentialsException e) {
            return Map.of();
        }

        String token = jwtUtil.generateToken(authenticationRequest.getLogin());
        return Map.of("jwt-token", token);
    }

    @ExceptionHandler
    private ResponseEntity<TodoErrorResponse> handleException(UserNotCreatedException e)
    {
        TodoErrorResponse todoErrorResponse = new TodoErrorResponse(e.getMessage());
        return new ResponseEntity<>(todoErrorResponse,HttpStatus.BAD_REQUEST);
    }
}
