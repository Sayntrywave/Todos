package com.korotkov.todo.controller;


import com.korotkov.todo.dto.response.UserResponse;
import com.korotkov.todo.dto.response.TodoResponse;
import com.korotkov.todo.dto.request.TodoRequest;
import com.korotkov.todo.dto.response.LoginResponse;
import com.korotkov.todo.model.Todo;
import com.korotkov.todo.service.TodoService;
import com.korotkov.todo.service.UserService;
import com.korotkov.todo.util.TodoErrorResponse;
import com.korotkov.todo.util.TodoNotCreatedException;
import com.korotkov.todo.util.TodoNotFoundException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin(origins = {"http://localhost:4000","https://localhost:4000"},allowCredentials = "true")
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

    @GetMapping("/users")
    public ResponseEntity<List<LoginResponse>> getUsers(){
        return new ResponseEntity<>(userService.getListOfUsers().stream().
                map(user -> modelMapper.map(user, LoginResponse.class))
                .collect(Collectors.toList()),HttpStatus.OK);
    }

    @GetMapping("/admin")
    public String getAdminMessage(){
        return "I'm admin";
    }

    @GetMapping("/me")
    public LoginResponse getInfo(){
        System.out.println("ПРОШЕЛ");
        return modelMapper.map(userService.getCurrentUser(), LoginResponse.class);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<TodoResponse>> getTodos(){
        List<Todo> todos = todoService.getAll();
        return new ResponseEntity<>(todos.stream().
                map(this::getTodoResponse)
                .collect(Collectors.toList()),HttpStatus.OK);
    }

    @PostMapping("/todos")
    public ResponseEntity<TodoResponse> createTodo(@RequestBody @Valid TodoRequest todoRequest,
                                                   BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new TodoNotCreatedException(bindingResult.getFieldError().getField() + " is empty");
        }

        Todo todo = modelMapper.map(todoRequest, Todo.class);
        todo.setCreatedBy(userService.getCurrentUser());
        todoService.save(todo);
        return new ResponseEntity<>(getTodoResponse(todo),HttpStatus.valueOf(201)) ;
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable int id){
        return new ResponseEntity<>(getTodoResponse(todoService.getById(id)),HttpStatus.valueOf(200));
    }

    @PutMapping("/todos/{id}")
    public HttpStatus updateTodo(@RequestBody @Valid TodoRequest todoRequest, BindingResult bindingResult, @PathVariable int id){
        if(bindingResult.hasErrors()){
            throw new TodoNotCreatedException(bindingResult.getFieldError().getField());
        }
        todoService.update(modelMapper.map(todoRequest,Todo.class ),id, userService.getCurrentUser());
        return HttpStatus.NO_CONTENT;
    }
    @DeleteMapping("/todos/{id}")
    public HttpStatus deleteTodo(@PathVariable int id) {
        todoService.delete(id);
        return HttpStatus.NO_CONTENT;
    }


    private TodoResponse getTodoResponse(Todo todo) {
        TodoResponse newTodo = modelMapper.map(todo, TodoResponse.class);
        newTodo.setCreator(modelMapper.map(todo.getCreatedBy(), UserResponse.class));
        return newTodo;
    }


    @GetMapping("/test")
    public String test(){
        System.out.println("ДОШЛО");
        return "test message";
    }


    @ExceptionHandler
    private ResponseEntity<HttpStatus> handleException(TodoNotFoundException e){
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    private ResponseEntity<TodoErrorResponse> handleException(TodoNotCreatedException e)
    {
        TodoErrorResponse todoErrorResponse = new TodoErrorResponse(e.getMessage());
        return new ResponseEntity<>(todoErrorResponse,HttpStatus.BAD_REQUEST);
    }

//    private User getCurrentUser(){}

    //todo add exception handler
}
