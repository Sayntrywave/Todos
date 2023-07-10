package com.korotkov.todo.service;

import com.korotkov.todo.model.Todo;
import com.korotkov.todo.model.User;
import com.korotkov.todo.repository.TodoRepository;
import com.korotkov.todo.util.TodoNotCreatedException;
import com.korotkov.todo.util.TodoNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserService userService;

    @Autowired
    public TodoService(TodoRepository todoRepository, UserService userService) {
        this.todoRepository = todoRepository;
        this.userService = userService;
    }


    public Todo getById(int id){

        if (todoRepository.existsById(id)) {
            return todoRepository.getReferenceById(id);
        }
        throw new TodoNotFoundException();
    }

    public List<Todo> getAll(){
        return todoRepository.findAll();
    }

    @Transactional
    public void save(Todo todo){
        String title = todo.getTitle();
        int id = todo.getId();
        if (todoRepository.existsTodoByTitleAndIdIsNot(title,id)) {
            throw new TodoNotCreatedException("title <" + title + "> isn't unique");
        }

        todoRepository.save(todo);
    }

    @Transactional
    public void update(Todo todo, int id, User currentUser){
        int currUserId = currentUser.getId();

        Todo todoById = getById(id);

        if(todoById.getTitle().equals(todo.getTitle()) == todoById.getDescription().equals(todo.getDescription())){
            return;
        }

        User creatorTodo = todoById.getCreatedBy(); // in db by id

        if(currentUser.getRole().equals("ROLE_ADMIN") || creatorTodo.getId() == currUserId){
            todo.setId(id);
            todo.setCreatedBy(creatorTodo);
            save(todo); //save
        }
    }

    @Transactional
    public void delete(int id){
        todoRepository.delete(getById(id));
    }


}
