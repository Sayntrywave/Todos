package com.korotkov.todo.controller;


import com.korotkov.todo.dto.TodoDTO;
import com.korotkov.todo.dto.UserDTO;
import com.korotkov.todo.model.Todo;
import com.korotkov.todo.service.TodoService;
import com.korotkov.todo.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public List<UserDTO> getUsers(){
        return userService.getListOfUsers().stream().
                map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/me")
    public UserDTO getInfo(){
        System.out.println("ПРОШЕЛ");
        return modelMapper.map(userService.getCurrentUser(), UserDTO.class);
    }

    @GetMapping("/todos")
    public List<TodoDTO> getTodos(){
        System.out.println("123");
        List<Todo> todos = userService.getTodos(userService.getCurrentUser());
        return todos.stream().
                map(todo -> modelMapper.map(todo, TodoDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping("/todos")
    public HttpStatus createTodo(@RequestBody TodoDTO todoDTO){
        Todo todo = modelMapper.map(todoDTO, Todo.class);
        todo.setCreatedBy(userService.getCurrentUser());
        todoService.save(todo);
        return HttpStatus.OK;
    }

    @GetMapping("/todo/{id}")
    public TodoDTO getTodo(@PathVariable int id){
        return modelMapper.map(todoService.getById(id),TodoDTO.class);
    }


    @GetMapping("/test")
    public String test(){
        System.out.println("ДОШЛО");
        return "test message";
    }

//    private User getCurrentUser(){}

    //todo add exception handler
}
