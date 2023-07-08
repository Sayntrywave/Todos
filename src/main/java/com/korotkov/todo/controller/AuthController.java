package com.korotkov.todo.controller;

import com.korotkov.todo.dto.AuthenticationDTO;
import com.korotkov.todo.model.User;
import com.korotkov.todo.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:4000","https://localhost:4000"},allowCredentials = "true")
@RestController
public class AuthController {
    private final RegistrationService service;
    private final AuthenticationManager authenticationManager;


    @Autowired
    public AuthController(RegistrationService service, AuthenticationManager authenticationManager) {
        this.service = service;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/hello")
    public String printHello(){
        return "Hello";
    }

    // TODO: 07.07.2023   add validation

    @PostMapping("/register")
    public HttpStatus registration(@RequestBody User user){
        service.register(user);
        return HttpStatus.OK;
    }
    @PostMapping("/login")
    public HttpStatus login (@RequestBody AuthenticationDTO authenticationDTO){
        System.out.println("ЗАЛОГИНИЛСЯ");
        UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(
                authenticationDTO.getUsername(),
                authenticationDTO.getPassword()
        );

        try {
            Authentication authenticate = authenticationManager.authenticate(authInputToken);
        } catch (BadCredentialsException e) {
            return HttpStatus.BAD_REQUEST;
        }

        return HttpStatus.OK;
    }
}
