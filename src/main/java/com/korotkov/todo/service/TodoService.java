package com.korotkov.todo.service;

import com.korotkov.todo.model.Todo;
import com.korotkov.todo.model.User;
import com.korotkov.todo.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TodoService {
    private final TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }


    public Todo getById(int id){
        return todoRepository.getReferenceById(id);
    }

    @Transactional
    public void save(Todo todo){
        todoRepository.save(todo);
    }



    @Transactional
    public void assign(int id, User user){

    }
}
