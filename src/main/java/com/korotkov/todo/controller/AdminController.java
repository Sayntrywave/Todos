package com.korotkov.todo.controller;

import com.korotkov.todo.dto.request.UserRequest;
import com.korotkov.todo.dto.response.LoginResponse;
import com.korotkov.todo.dto.response.UserResponse;
import com.korotkov.todo.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class AdminController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public AdminController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/admin")
    public String getAdminMessage(){
        return "I'm admin";
    }

    @GetMapping("/users")
    public ResponseEntity<List<LoginResponse>> getUsers(){
        return new ResponseEntity<>(userService.getListOfUsers().stream().
                map(user -> modelMapper.map(user, LoginResponse.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

//    @GetMapping("/user/{id}")
//    public ResponseEntity<UserResponse> getUser(@PathVariable("id") int id){
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

//    @PutMapping("/user/{id}")
//    public ResponseEntity<UserResponse> editUser(@RequestBody @Valid UserRequest userRequest , @PathVariable("id") int id){
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

}
