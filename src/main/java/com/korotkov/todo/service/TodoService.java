package com.korotkov.todo.service;

import com.korotkov.todo.model.*;
import com.korotkov.todo.repository.RoleRepository;
import com.korotkov.todo.repository.TodoRepository;
import com.korotkov.todo.repository.TodoRequestRepository;
import com.korotkov.todo.repository.TodoUserRepository;
import com.korotkov.todo.util.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class TodoService {

    private final TodoUserRepository todoUserRepository;
    private final TodoRepository todoRepository;

    private final RoleRepository roleRepository;
    private final UserService userService;
    private TodoRequestRepository todoRequestRepository;

    @Autowired
    public TodoService(TodoUserRepository todoUserRepository,
                       TodoRepository todoRepository,
                       RoleRepository roleRepository,
                       UserService userService,
                       TodoRequestRepository todoRequestRepository,
                       CacheManager cacheManager) {
        this.todoUserRepository = todoUserRepository;
        this.todoRepository = todoRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.todoRequestRepository = todoRequestRepository;
        this.cacheManager = cacheManager;
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


    @Cacheable(value = "todos")
    public List<TodoUser> getTodoUser() {
//        System.out.println(cacheManager.getCache("users"));

        List<TodoUser> todoUserList = todoUserRepository.findAll();
//        new ConcurrentMapCache("users",)
        return todoUserList;
    }
    private final CacheManager cacheManager;

    public Map<User, Privilege> getUserAndTheirRoles() {
        List<TodoUser> todoUserList = todoUserRepository.findAll();
        Map<User, Privilege> result = new HashMap<>();
        todoUserList.forEach(todoUser -> result.put(todoUser.getUser(),
                todoUser.getPrivilege()));

        return result;
    }
    @Transactional
    public void save(Todo todo, User creator) {

        String title = todo.getTitle();
        int id = todo.getId();
        if (todoRepository.existsTodoByTitleAndIdIsNot(title, id)) {
            throw new TodoNotCreatedException("title <" + title + "> isn't unique");
        }
        cacheManager.getCache("todos").evict(new SimpleKey());

        todoRepository.save(todo);
//        Cache users = cacheManager.getCache("users").;
        if (todo.getCreator() == null) {
            roleRepository.getRoleByName("CREATOR").ifPresent(role -> todoUserRepository.save(new TodoUser(todo, creator, role)));
        }
    }
//    @CachePut("users")

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

        Privilege privilege = todoUserByUserIdAndTodoId.orElseThrow(() -> new TodoBadCredentialException("this user isn't related with this todo")).getPrivilege();

        //check rights
        // c || a && b
        TodoAction todoAction = (todo.getIsCompleted() != null) ? TodoAction.COMPLETE : TodoAction.EDIT;
        if (Privilege.canEditTodo(privilege, todoAction)) {
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
            throw new UserHasNoPrivilegeException("you don't have privilege of todoAction " + todoAction);
        }
    }


    @Transactional
    public void setUser(Todo todo, String privilege, User from, User to) {
        privilege = privilege.toUpperCase();
        String finalPrivilege = privilege;
        roleRepository.getRoleByName(privilege).ifPresent(role ->
                todoUserRepository.getTodoUserByUserIdAndTodoId(from.getId(), todo.getId()).ifPresent(todoUser -> {
                    Privilege privilege1 = todoUser.getPrivilege();
                    if (Privilege.canSetPrivilege(privilege1, role)) {
                        if (finalPrivilege.equals("NONE")){
                            todoUserRepository.deleteByUserId(to.getId());
                        }
                        else {
                            Optional<TodoUser> tu = todoUserRepository.getTodoUserByUserIdAndTodoId(to.getId(), todo.getId());
                            if(tu.isPresent()){
                                TodoUser todoUser2 = tu.get();
                                todoUser2.setPrivilege(role);
                                todoUserRepository.save(todoUser2);
                                cacheManager.getCache("todos").evict(new SimpleKey());
                            }
                            else {
                                TodoRequest todoRequest = new TodoRequest(todo, to, role);
                                todoRequestRepository.save(todoRequest);
                            }
//                            cacheManager.getCache("todos").evict(new SimpleKey());
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

        Privilege privilege = todoUserByUserIdAndTodoId.orElseThrow(() -> new TodoBadCredentialException("this user isn't related with this todo")).getPrivilege();


        TodoAction todoAction = TodoAction.DELETE;
        if(Privilege.canEditTodo(privilege, todoAction)){
            cacheManager.getCache("todos").evict(new SimpleKey());
            todoRepository.delete(getById(id));

        }
        else {
            throw new UserHasNoPrivilegeException("you don't have privilege of todoAction " + todoAction);
        }
    }

}
