package com.korotkov.todo.controller;

import com.korotkov.todo.dto.request.UserEditRequest;
import com.korotkov.todo.dto.response.UserResponse;
import com.korotkov.todo.model.User;
import com.korotkov.todo.security.JWTUtil;
import com.korotkov.todo.service.UserService;
import com.korotkov.todo.util.TodoErrorResponse;
import com.korotkov.todo.util.UserHasNoRightsException;
import com.korotkov.todo.util.UserNotFoundException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class AdminController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final JWTUtil jwtUtil;

    @Autowired
    public AdminController(UserService userService, ModelMapper modelMapper, JWTUtil jwtUtil) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/admin")
    public String getAdminMessage(){
        return "I'm admin";
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers(){
        return new ResponseEntity<>(userService.getListOfUsers().stream().
                map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }


    @PutMapping("/user/{id}")
    public ResponseEntity<Map<String,String>> editUser(@RequestBody @Valid UserEditRequest userEditRequest,
                                        BindingResult bindingResult,
                                        @PathVariable("id") int id){
        if (bindingResult.hasErrors()) {
            throw new BadCredentialsException(bindingResult.getFieldError().getField() + " is empty");
        }
        User currentUser = userService.getCurrentUser();
        boolean update = userService.update(modelMapper.map(userEditRequest, User.class), id, currentUser);
        if (update){
            String token = jwtUtil.generateToken(userEditRequest.getLogin());
            return new ResponseEntity<>(Map.of("jwt-token", token), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("/ban/{id}")
    public HttpStatus banUser(@PathVariable("id") int id){
        userService.makeBannedById(id,userService.getCurrentUser());
        return HttpStatus.OK;
    }

    @ExceptionHandler
    private ResponseEntity<TodoErrorResponse> handleException(UserHasNoRightsException e)
    {
        TodoErrorResponse todoErrorResponse = new TodoErrorResponse(e.getMessage());
        return new ResponseEntity<>(todoErrorResponse,HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler
    private ResponseEntity<TodoErrorResponse> handleException(BadCredentialsException e)
    {
        TodoErrorResponse todoErrorResponse = new TodoErrorResponse(e.getMessage());
        return new ResponseEntity<>(todoErrorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<TodoErrorResponse> handleException(UserNotFoundException e)
    {
        TodoErrorResponse todoErrorResponse = new TodoErrorResponse(e.getMessage());
        return new ResponseEntity<>(todoErrorResponse,HttpStatus.BAD_REQUEST);
    }





}
