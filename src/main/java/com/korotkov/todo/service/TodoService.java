package com.korotkov.todo.service;

import com.korotkov.todo.model.*;
import com.korotkov.todo.repository.RoleRepository;
import com.korotkov.todo.repository.TodoRepository;
import com.korotkov.todo.repository.TodoUserRepository;
import com.korotkov.todo.util.TodoNotCreatedException;
import com.korotkov.todo.util.TodoNotFoundException;
import com.korotkov.todo.util.UserHasNoRightsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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


    public Todo getById(int id) {

        if (todoRepository.existsById(id)) {
            return todoRepository.getReferenceById(id);
        }
        throw new TodoNotFoundException();
    }

    public List<TodoUser> getByIdTodoUser(int id) {
        List<TodoUser> todo = todoUserRepository.getTodoUsersByTodoId(id);
        if (todo.isEmpty()) {
            throw new TodoNotFoundException();
        }
        return todo;

    }

    public List<Todo> getAll() {
        return todoRepository.findAll();
    }

    public List<TodoUser> getTodoUser() {
        return todoUserRepository.findAll();
    }

    public Map<User, Role> getUserAndTheirRoles() {
        List<TodoUser> todoUserList = todoUserRepository.findAll();
        Map<User, Role> result = new HashMap<>();
        todoUserList.forEach(todoUser -> result.put(todoUser.getUser(),
                todoUser.getRole()));

        return result;
    }

    @Transactional
    public void save(Todo todo, User creator) {

        String title = todo.getTitle();
        int id = todo.getId();
        if (todoRepository.existsTodoByTitleAndIdIsNot(title, id)) {
            throw new TodoNotCreatedException("title <" + title + "> isn't unique");
        }

        todoRepository.save(todo);
        if (todo.getCreator() == null) {
            roleRepository.getRoleByName("CREATOR").ifPresent(role -> todoUserRepository.save(new TodoUser(todo, creator, role)));
        }
    }

    @Transactional
    public void save(Todo todo) {
        String title = todo.getTitle();
        int id = todo.getId();
        if (todoRepository.existsTodoByTitleAndIdIsNot(title, id)) {
            throw new TodoNotCreatedException("title <" + title + "> isn't unique");
        }

        todoRepository.save(todo);
    }

    @Transactional
    public void update(Todo todo, int id, User currentUser) {
        int currUserId = currentUser.getId();

         Todo todoById = getById(id);


        if (isAllFieldsNull(todo)) {
            return;
        }


        User creatorTodo = todoById.getCreator(); // in db by id

        Optional<TodoUser> todoUserByUserIdAndTodoId = todoUserRepository.getTodoUserByUserIdAndTodoId(currUserId,id);

        Role role = todoUserByUserIdAndTodoId.orElseThrow(() -> new BadCredentialsException("this user isn't related with this todo")).getRole();

        //check rights
        // c || a && b
        RoleAction action = (todo.getIsCompleted() != null) ? RoleAction.COMPLETE : RoleAction.EDIT;
        if (Role.canEditTodo(role,action)) {
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
            save(todoById, creatorTodo); //save
        } else {
            throw new UserHasNoRightsException("you don't have privilege of action " + action);
        }
    }

    @Transactional
    public void setUser(Todo todo, String privilege, User from, User to) {
        privilege = privilege.toUpperCase();
        String finalPrivilege = privilege;
        roleRepository.getRoleByName(privilege).ifPresent(role ->
                todoUserRepository.getTodoUserByUserIdAndTodoId(from.getId(), todo.getId()).ifPresent(todoUser -> {
                    Role role1 = todoUser.getRole();
                    if (Role.canSetRole(role1, role)) {
                        if (finalPrivilege.equals("NONE")){
                            todoUserRepository.deleteByUserId(to.getId());
                        }
                        else {
                            TodoUser todoUser2;
                            Optional<TodoUser> tu = todoUserRepository.getTodoUserByUserIdAndTodoId(to.getId(), todo.getId());
                            if(tu.isPresent()){
                                todoUser2 = tu.get();
                                todoUser2.setRole(role);
                            }
                            else {
                                todoUser2 = new TodoUser(todo, to, role);
                            }
                            todoUserRepository.save(todoUser2);
                        }
                    }
                })
        );

    }

    private boolean isAllFieldsNull(Todo todo) {
        return todo.getIsCompleted() == null && todo.getTitle() == null && todo.getDescription() == null &&
                todo.getTimeSpent() == null;
    }



    @Transactional
    public void delete(User updatedBy,int id) {
        Optional<TodoUser> todoUserByUserIdAndTodoId = todoUserRepository.getTodoUserByUserIdAndTodoId(updatedBy.getId(),id);

        Role role = todoUserByUserIdAndTodoId.orElseThrow(() -> new BadCredentialsException("this user isn't related with this todo")).getRole();


        RoleAction action = RoleAction.DELETE;
        if(Role.canEditTodo(role, action)){
            todoRepository.delete(getById(id));

        }
        else {
            throw new UserHasNoRightsException("you don't have privilege of action " + action);
        }
    }

}
