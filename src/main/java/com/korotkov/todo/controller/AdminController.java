package com.korotkov.todo.controller;

import com.korotkov.todo.dto.request.UserEditRequest;
import com.korotkov.todo.dto.response.UserResponse;
import com.korotkov.todo.model.Role;
import com.korotkov.todo.model.User;
import com.korotkov.todo.repository.RoleRepository;
import com.korotkov.todo.security.JWTUtil;
import com.korotkov.todo.service.RegistrationService;
import com.korotkov.todo.service.TodoService;
import com.korotkov.todo.service.UserService;
import com.korotkov.todo.util.errorResponse.TodoErrorResponse;
import com.korotkov.todo.util.errorResponse.UserErrorResponse;
import com.korotkov.todo.util.exception.UserHasNoRightsException;
import com.korotkov.todo.util.exception.UserNotCreatedException;
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
    private final RoleRepository roleRepository;

    private final TodoService todoService;

    private final RegistrationService registrationService;


    @Autowired
    public AdminController(UserService userService,
                           ModelMapper modelMapper,
                           JWTUtil jwtUtil,
                           RoleRepository roleRepository,
                           TodoService todoService, RegistrationService registrationService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
        this.roleRepository = roleRepository;
        this.todoService = todoService;
        this.registrationService = registrationService;
    }

    @GetMapping("/admin")
    public String getAdminMessage() {
        return "I'm admin";
    }

    //200 тысяч единиц уже готовы еще миллион на подходе
    @PostMapping("/admin/user-factory")
    public ResponseEntity<HttpStatus> makeUsers(@RequestParam(value = "count", defaultValue = "1") int count) {
        registrationService.register(count);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/admin/todo-factory")
    public ResponseEntity<HttpStatus> makeTodo(@RequestParam(value = "count", defaultValue = "1") int count) {
        todoService.createTodosForAllUsers(count);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return new ResponseEntity<>(userService.getListOfUsers().stream().
                map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }


    @PutMapping("/user/{id}")
    public ResponseEntity<Map<String, String>> editUser(@RequestBody @Valid UserEditRequest userEditRequest,
                                                        BindingResult bindingResult,
                                                        @PathVariable("id") int id) {

        if (bindingResult.hasErrors()) {
            throw new BadCredentialsException(bindingResult.getFieldError().getField() + bindingResult.getFieldError().getDefaultMessage());
        }
        User currentUser = userService.getCurrentUser();

        //todo modify it pls
        User map = modelMapper.map(userEditRequest, User.class);
        if (userEditRequest.getRole() != null) {
            Role roleByName = roleRepository.getRoleByName(userEditRequest.getRole())
                    .orElseThrow(() -> new BadCredentialsException("not found this role: " + currentUser.getRole()));
            map.setRole(roleByName);
        }

        boolean update = userService.update(map, id, currentUser);
        if (update) {
            String token = jwtUtil.generateToken(userEditRequest.getLogin());
            return new ResponseEntity<>(Map.of("token", token), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserHasNoRightsException e) {
        UserErrorResponse userErrorResponse = new UserErrorResponse(e.getMessage());
        return new ResponseEntity<>(userErrorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    private ResponseEntity<TodoErrorResponse> handleException(UserNotCreatedException e) {
        TodoErrorResponse todoErrorResponse = new TodoErrorResponse(e.getMessage());
        return new ResponseEntity<>(todoErrorResponse, HttpStatus.FORBIDDEN);
    }


}
