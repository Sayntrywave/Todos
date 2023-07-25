package com.korotkov.todo.service;

import com.korotkov.todo.model.Todo;
import com.korotkov.todo.model.TodoUser;
import com.korotkov.todo.model.User;
import com.korotkov.todo.repository.RoleRepository;
import com.korotkov.todo.repository.TodoRepository;
import com.korotkov.todo.repository.TodoUserRepository;
import com.korotkov.todo.util.TodoNotCreatedException;
import com.korotkov.todo.util.TodoNotFoundException;
import com.korotkov.todo.util.UserHasNoRightsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TodoService {

    private final TodoUserRepository todoUserRepository;
    private final TodoRepository todoRepository;

    private final RoleRepository roleRepository;
    private final UserService userService;

    @Autowired
    public TodoService(TodoUserRepository todoUserRepository, TodoRepository todoRepository, RoleRepository roleRepository, UserService userService) {
        this.todoUserRepository = todoUserRepository;
        this.todoRepository = todoRepository;
        this.roleRepository = roleRepository;
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
    public void save(Todo todo, User creator){

        String title = todo.getTitle();
        int id = todo.getId();
        if (todoRepository.existsTodoByTitleAndIdIsNot(title,id)) {
            throw new TodoNotCreatedException("title <" + title + "> isn't unique");
        }

        todoRepository.save(todo);
        if(todo.getCreator() == null){
            roleRepository.getRoleByName("CREATOR").ifPresent(role -> todoUserRepository.save(new TodoUser(todo,creator,role)));
        }
    }

    @Transactional
    public void update(Todo todo, int id, User currentUser){
        int currUserId = currentUser.getId();

        Todo todoById = getById(id);


        if(isAllFieldsNull(todo)){
            return;
        }


        User creatorTodo = todoById.getCreator(); // in db by id

        //check rights
        if(creatorTodo.getId() == currUserId){
            String title = todo.getTitle();
            if (title != null) {
                todoById.setTitle(title);
            }
            String Description = todo.getDescription();
            if (Description != null) {
                todoById.setDescription(Description);
            }
            Integer timeSpent = todo.getTimeSpent();
            if (timeSpent != null) {
                todoById.setTimeSpent(timeSpent);
            }
            Boolean isCompleted = todo.getIsCompleted();
            if (isCompleted != null) {
                todoById.setIsCompleted(isCompleted);
            }
            save(todoById,creatorTodo); //save
        }
        else {
            throw new UserHasNoRightsException("you can't edit todos of other users");
        }
    }

    public void setUser(Todo todo, String privilege, User from, User to) {
//        roleRepository.getRoleByName(privilege).ifPresent(role -> {
//            if(todo.getUsersByRole().)
//        });
    }

    private boolean isAllFieldsNull(Todo todo){
        return todo.getIsCompleted() == null && todo.getTitle() == null && todo.getDescription() == null &&
                todo.getTimeSpent() == null;
    }

    @Transactional
    public void delete(int id){
        todoRepository.delete(getById(id));
    }

}
