package com.korotkov.todo.controller;


import com.korotkov.todo.dto.request.TodoUserRequest;
import com.korotkov.todo.dto.request.TodoRequest;
import com.korotkov.todo.dto.response.LoginResponse;
import com.korotkov.todo.dto.response.TodoResponse;
import com.korotkov.todo.dto.response.UserResponse;
import com.korotkov.todo.dto.response.UserTodo;
import com.korotkov.todo.model.Todo;
import com.korotkov.todo.model.TodoUser;
import com.korotkov.todo.model.User;
import com.korotkov.todo.service.TodoService;
import com.korotkov.todo.service.UserService;
import com.korotkov.todo.util.TodoErrorResponse;
import com.korotkov.todo.util.TodoNotCreatedException;
import com.korotkov.todo.util.TodoNotFoundException;
import com.korotkov.todo.util.UserHasNoRightsException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final TodoService todoService;
    private final ModelMapper modelMapper;

//    @Value("${key-password}")
//    private String value;

    @Autowired
    public UserController(UserService userService, TodoService todoService, ModelMapper modelMapper) {
        this.userService = userService;
        this.todoService = todoService;
        this.modelMapper = modelMapper;
    }


    @GetMapping("/me")
    public LoginResponse getInfo() {
        LoginResponse map = modelMapper.map(userService.getCurrentUser(), LoginResponse.class);
        map.setRole("ADMIN");
        return map;
    }


    @GetMapping("/todos")
    public ResponseEntity<List<TodoResponse>> getTodos() {
        List<TodoUser> todos = todoService.getTodoUser();
        return new ResponseEntity<>(getTodosResponse(todos), HttpStatus.OK);
    }

    @PostMapping("/todos")
    public ResponseEntity<TodoResponse> createTodo(@RequestBody @Valid TodoRequest todoRequest,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new TodoNotCreatedException(bindingResult.getFieldError().getField() + " " + bindingResult.getFieldError().getDefaultMessage());
        }

        Todo todo = modelMapper.map(todoRequest, Todo.class);
        User currentUser = userService.getCurrentUser();
        todoService.save(todo, currentUser);
        return new ResponseEntity<>(getTodosResponse(todo, currentUser), HttpStatus.valueOf(201));
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable int id) {
        return new ResponseEntity<>(getTodoResponse(todoService.getByIdTodoUser(id)), HttpStatus.valueOf(200));
    }

    @PutMapping("/todos/{id}")
    public HttpStatus updateTodo(@RequestBody @Valid TodoRequest todoRequest,
                                 BindingResult bindingResult,
                                 @PathVariable int id) {
//,
//                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token
//        System.out.println(token);
        // login password -> db
        //
        if (bindingResult.hasErrors()) {
            throw new TodoNotCreatedException(bindingResult.getFieldError().getField() + " is empty or null");
        }
        todoService.update(modelMapper.map(todoRequest, Todo.class), id, userService.getCurrentUser());
        return HttpStatus.NO_CONTENT;
    }

    @DeleteMapping("/todos/{id}")
    public HttpStatus deleteTodo(@PathVariable int id) {
        todoService.delete(userService.getCurrentUser(),id);
        return HttpStatus.NO_CONTENT;
    }


    @PutMapping("/todo/{id}/privileges")
    public HttpStatus setPrivileges(@RequestBody @Valid TodoUserRequest userRequest,
                                    BindingResult bindingResult,
                                    @PathVariable int id){

        if (bindingResult.hasErrors()){
            //todo exception handling
        }
        Todo byId = todoService.getById(id);

        User from = userService.getCurrentUser();
        User to = userService.findByLogin(userRequest.getLogin());
        todoService.setUser(byId,userRequest.getPrivilege(),from,to);
        return HttpStatus.NO_CONTENT;
    }


    private TodoResponse getTodosResponse(Todo todoUser, User creator) {
        TodoResponse newTodo = modelMapper.map(todoUser, TodoResponse.class);
        List<UserTodo> userResponse = new ArrayList<>();
        UserTodo map = modelMapper.map(creator, UserTodo.class);
        map.setPrivilege("CREATOR");
        userResponse.add(map);
        newTodo.setUsers(userResponse);
        return newTodo;
    }
    private List<TodoResponse> getTodosResponse(List<TodoUser> todoUser) {
//        MultiMap<Todo, UserTodo> userTodoMap = new MultiMap<>();

        MultiValueMapAdapter<Todo,UserTodo> userTodoMap = new MultiValueMapAdapter<>(new HashMap<>());
        for (TodoUser user : todoUser) {

            userTodoMap.add(user.getTodo(),
                    new UserTodo(modelMapper.map(user.getUser(), UserResponse.class),
                            user.getRole().getName()));
        }

        List<TodoResponse> response = new ArrayList<>();
//
//        for (Map.Entry<Todo, UserTodo> entry : userTodoMap.entrySet()) {
//            response.add(new TodoResponse(entry.getKey(),e));
//        }
        for (Map.Entry<Todo, List<UserTodo>> entry : userTodoMap.entrySet()) {

            TodoResponse todoResponse = modelMapper.map(entry.getKey(), TodoResponse.class);
            todoResponse.setUsers(entry.getValue());
            response.add(todoResponse);
        }
//
//        TodoResponse newTodo = modelMapper.map(todoUser.getTodo(), TodoResponse.class);
//        List<UserTodo> userResponse = todoUser.getUsers().entrySet().stream().map(entry -> {
//
//            UserTodo map = modelMapper.map(entry.getKey(), UserTodo.class);
//            map.setRole(entry.getValue().getRole().getName());
//            return map ;
//        }).toList();
//        newTodo.setUsers(userResponse);
        return response;
    }

    private TodoResponse getTodoResponse(List<TodoUser> todoUser) {
        return getTodosResponse(todoUser).get(0);
    }


    @GetMapping("/test")
    public String test() {
//        System.out.println(value);
//        System.out.println(ManagementFactory.getRuntimeMXBean().getInputArguments());
        return "test message";
    }


    @ExceptionHandler
    private ResponseEntity<HttpStatus> handleException(TodoNotFoundException e) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<TodoErrorResponse> handleException(UserHasNoRightsException e) {
        TodoErrorResponse todoErrorResponse = new TodoErrorResponse(e.getMessage());
        return new ResponseEntity<>(todoErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<TodoErrorResponse> handleException(TodoNotCreatedException e) {
        TodoErrorResponse todoErrorResponse = new TodoErrorResponse(e.getMessage());
        return new ResponseEntity<>(todoErrorResponse, HttpStatus.BAD_REQUEST);
    }

//    private User getCurrentUser(){}

    //todo add exception handler
}
