package com.korotkov.todo.controller;


import com.korotkov.todo.dto.request.AcceptRequest;
import com.korotkov.todo.dto.request.TodoRequestDTO;
import com.korotkov.todo.dto.request.TodoUserRequest;
import com.korotkov.todo.dto.response.*;
import com.korotkov.todo.model.Todo;
import com.korotkov.todo.model.TodoRequest;
import com.korotkov.todo.model.TodoUser;
import com.korotkov.todo.model.User;
import com.korotkov.todo.service.TodoService;
import com.korotkov.todo.service.UserService;
import com.korotkov.todo.util.errorResponse.TodoErrorResponse;
import com.korotkov.todo.util.errorResponse.UserErrorResponse;
import com.korotkov.todo.util.exception.TodoNotCreatedException;
import com.korotkov.todo.util.exception.TodoNotFoundException;
import com.korotkov.todo.util.exception.UserHasNoPrivilegeException;
import com.korotkov.todo.util.exception.UserHasNoRightsException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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

    @Autowired
    public UserController(UserService userService, TodoService todoService, ModelMapper modelMapper) {
        this.userService = userService;
        this.todoService = todoService;
        this.modelMapper = modelMapper;
    }


    @GetMapping("/me")
    public LoginResponse getInfo() {
        LoginResponse map = modelMapper.map(userService.getCurrentUser(), LoginResponse.class);
//        map.setRole("ADMIN");
        return map;
    }

    @GetMapping("/todos-count")
    public ResponseEntity<Map<String, Long>> getTodosCount(@RequestParam(value = "q", required = false) String query){
        return new ResponseEntity<>(Map.of("count",todoService.getCount(userService.getCurrentUser().getId(),query)),HttpStatus.OK);
    }


    @GetMapping("/todos")
    public ResponseEntity<List<TodoResponse>> getTodos(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort,
            @RequestParam(value = "q", required = false) String query) {

        User currentUser = userService.getCurrentUser();
        List<TodoUser> todos = todoService.getTodoUser(currentUser.getId(),page - 1,limit,sort,query);
        return new ResponseEntity<>(getTodosResponse(todos), HttpStatus.OK);
    }

    @PostMapping("/todos")
    public ResponseEntity<TodoResponse> createTodo(@RequestBody @Valid TodoRequestDTO todoRequestDTO,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new TodoNotCreatedException(bindingResult.getFieldError().getField() + " " + bindingResult.getFieldError().getDefaultMessage());
        }

        Todo todo = modelMapper.map(todoRequestDTO, Todo.class);
        User currentUser = userService.getCurrentUser();
        todoService.save(todo, currentUser);
        return new ResponseEntity<>(getTodosResponse(todo, currentUser), HttpStatus.valueOf(201));
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable int id) {
        return new ResponseEntity<>(getTodoResponse(todoService.getByIdTodoUser(id)), HttpStatus.valueOf(200));
    }

    @PutMapping("/todos/{id}")
    public HttpStatus updateTodo(@RequestBody @Valid TodoRequestDTO todoRequestDTO,
                                 BindingResult bindingResult,
                                 @PathVariable int id) {
        if (bindingResult.hasErrors()) {
            throw new TodoNotCreatedException(bindingResult.getFieldError().getField() + " is empty or null");
        }
        todoService.update(modelMapper.map(todoRequestDTO, Todo.class), id, userService.getCurrentUser());
        return HttpStatus.NO_CONTENT;
    }

    @DeleteMapping("/todos/{id}")
    public HttpStatus deleteTodo(@PathVariable int id) {
        todoService.delete(userService.getCurrentUser(), id);
        return HttpStatus.NO_CONTENT;
    }


    @PutMapping("/todo/{id}/privileges")
    public HttpStatus setPrivileges(@RequestBody @Valid TodoUserRequest userRequest,
                                    BindingResult bindingResult,
                                    @PathVariable int id) {

        if (bindingResult.hasErrors()) {
            //todo exception handling
        }
        Todo byId = todoService.getById(id);

        User from = userService.getCurrentUser();
        User to = userService.findByLogin(userRequest.getLogin());
        todoService.setUser(byId, userRequest.getPrivilege(), from, to);
        return HttpStatus.NO_CONTENT;
    }

    @PostMapping("/todo/{id}/request")
    public HttpStatus acceptPrivilege(@RequestBody AcceptRequest accept, @PathVariable int id) {
        if (accept.getAccept() == null) {
            throw new BadCredentialsException("accept can't be null");
        }
        userService.acceptTodoRequest(id, userService.getCurrentUser(), accept.getAccept());

        return HttpStatus.OK;
    }

    @GetMapping("/requests")
    public ResponseEntity<List<TodoRequestResponse>> getTodoRequests() {
        User currentUser = userService.getCurrentUser();
        List<TodoRequest> todoRequests = userService.getTodoRequests(currentUser);
        return new ResponseEntity<>(getTodoRequestResponse(todoRequests), HttpStatus.OK);
    }

    private List<TodoRequestResponse> getTodoRequestResponse(List<TodoRequest> todoRequests) {
        List<TodoRequestResponse> responses = new ArrayList<>();

        for (TodoRequest request : todoRequests) {
            responses.add(new TodoRequestResponse(request));
        }

        return responses;
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

        MultiValueMapAdapter<Todo, UserTodo> userTodoMap = new MultiValueMapAdapter<>(new HashMap<>());
        for (TodoUser user : todoUser) {

            userTodoMap.add(user.getTodo(),
                    new UserTodo(modelMapper.map(user.getUser(), UserResponse.class),
                            user.getPrivilege().getName()));
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
//            map.setPrivilege(entry.getValue().getPrivilege().getName());
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
    private ResponseEntity<TodoErrorResponse> handleException(TodoNotFoundException e) {
        TodoErrorResponse todoErrorResponse = new TodoErrorResponse(e.getMessage());
        return new ResponseEntity<>(todoErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserHasNoRightsException e) {
        UserErrorResponse userErrorResponse = new UserErrorResponse(e.getMessage());
        return new ResponseEntity<>(userErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<TodoErrorResponse> handleException(UserHasNoPrivilegeException e) {
        TodoErrorResponse todoErrorResponse = new TodoErrorResponse(e.getMessage());
        return new ResponseEntity<>(todoErrorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler
    private ResponseEntity<TodoErrorResponse> handleException(TodoNotCreatedException e) {
        TodoErrorResponse todoErrorResponse = new TodoErrorResponse(e.getMessage());
        return new ResponseEntity<>(todoErrorResponse, HttpStatus.BAD_REQUEST);
    }

}
