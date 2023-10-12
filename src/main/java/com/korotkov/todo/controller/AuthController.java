package com.korotkov.todo.controller;

import com.korotkov.todo.dto.request.AuthenticationRequest;
import com.korotkov.todo.dto.request.RegistrationRequest;
import com.korotkov.todo.dto.response.LoginResponse;
import com.korotkov.todo.model.User;
import com.korotkov.todo.security.JWTUtil;
import com.korotkov.todo.service.RegistrationService;
import com.korotkov.todo.service.UserService;
import com.korotkov.todo.util.errorResponse.AuthErrorResponse;
import com.korotkov.todo.util.errorResponse.TodoErrorResponse;
import com.korotkov.todo.util.exception.UserNotCreatedException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@CrossOrigin
@RestController
public class AuthController {
    private final RegistrationService registrationService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final JWTUtil jwtUtil;


    @Autowired
    public AuthController(RegistrationService registrationService, UserService userService, AuthenticationManager authenticationManager, ModelMapper modelMapper, JWTUtil jwtUtil) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/hello")
    public String printHello() {
        return "Hello";
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> registration(@RequestBody @Valid RegistrationRequest user,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new UserNotCreatedException(bindingResult.getFieldError().getField() + " " + bindingResult.getFieldError().getDefaultMessage());
        }

        registrationService.register(modelMapper.map(user, User.class));

        User currentUser = userService.findByLogin(user.getLogin());

        String token = jwtUtil.generateToken(user.getLogin());
        LoginResponse map = modelMapper.map(currentUser, LoginResponse.class);
        map.setToken(token);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid AuthenticationRequest authenticationRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadCredentialsException(bindingResult.getFieldError().getDefaultMessage());
        }


        UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(
                authenticationRequest.getLogin(),
                authenticationRequest.getPassword()
        );

        authenticationManager.authenticate(authInputToken);


        User currentUser = userService.findByLogin(authenticationRequest.getLogin());

        String token = jwtUtil.generateToken(authenticationRequest.getLogin());


        LoginResponse map = modelMapper.map(currentUser, LoginResponse.class);
        map.setToken(token);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @ExceptionHandler
    private ResponseEntity<String> handleException(UserNotCreatedException e) {
        TodoErrorResponse todoErrorResponse = new TodoErrorResponse(e.getMessage());
        return new ResponseEntity<>(todoErrorResponse.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException(AuthenticationException e) {
        TodoErrorResponse todoErrorResponse = new TodoErrorResponse(e.getMessage());
        System.out.println(e.getMessage());
        return new ResponseEntity<>(todoErrorResponse.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<AuthErrorResponse> handleException(BadCredentialsException e) {
        AuthErrorResponse todoErrorResponse = new AuthErrorResponse(e.getMessage());
        return new ResponseEntity<>(todoErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
