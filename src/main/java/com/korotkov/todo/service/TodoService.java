package com.korotkov.todo.service;

import com.github.javafaker.Faker;
import com.korotkov.todo.model.*;
import com.korotkov.todo.repository.*;
import com.korotkov.todo.util.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TodoService {

    private final TodoUserRepository todoUserRepository;
    private final TodoRepository todoRepository;

//    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final UserService userService;
    private TodoRequestRepository todoRequestRepository;
    private final CacheManager cacheManager;


    @Autowired
    public TodoService(TodoUserRepository todoUserRepository,
                       TodoRepository todoRepository,
                       PrivilegeRepository privilegeRepository, UserService userService,
                       TodoRequestRepository todoRequestRepository,
                       CacheManager cacheManager) {
        this.todoUserRepository = todoUserRepository;
        this.todoRepository = todoRepository;
        this.privilegeRepository = privilegeRepository;
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

    public long getCount(int user_id, String query){
        if(query != null){
            return todoUserRepository.countTodoUsersByUserIdAndTodo_TitleIgnoreCaseContains(user_id,query);
        }
        return todoUserRepository.countTodoUsersByUserId(user_id);
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

//    @Cacheable(value = "todos")
    public List<TodoUser> getTodoUser(int userId, int page, int size, String sort, String query) {
//        if (query != null){
//            return todoUserRepository.getTodoUsersByUserId();
//        }
        List<TodoUser> collect;

        if (query != null){
            collect = todoUserRepository.
                    getTodoUsersByUserIdAndTodo_TitleIgnoreCaseContains(
                            userId,
                            query,
                            PageRequest.of(page, size, Sort.by(sort).descending()))
                    .get()
                    .collect(Collectors.toList());
            return collect;
        }
        else {
            collect = todoUserRepository.
                    getTodoUsersByUserId(
                            userId,
                            PageRequest.of(page,size,Sort.by(sort).descending()))
                    .get()
                    .collect(Collectors.toList());
        }

        int size1 = collect.size();
        for (int i = 0; i < size1; i++) {
            TodoUser todoUser = collect.get(i);
            List<TodoUser> todoUsersByTodoId = todoUserRepository.getTodoUsersByTodoId(todoUser.getTodo().getId());

            for (TodoUser user : todoUsersByTodoId) {
                if (user.getId() != todoUser.getId()) {
                    collect.add(user);
                }
            }
        }
        return collect;
    }

    public Map<User, Privilege> getUserAndTheirRoles() {
        List<TodoUser> todoUserList = todoUserRepository.findAll();
        Map<User, Privilege> result = new HashMap<>();
        todoUserList.forEach(todoUser -> result.put(todoUser.getUser(),
                todoUser.getPrivilege()));

        return result;
    }


    @Transactional
    public void createTodosForAllUsers(int count){
        Todo todo;
        Faker faker = new Faker();
        for (User user : userService.getListOfUsers()) {
            for (int i = 0; i < count; i++) {
                todo = new Todo();
                todo.setTitle(faker.name().name());
                todo.setDescription(faker.friends().quote());
                save(todo,user);
            }
        }
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
            privilegeRepository.getPrivilegeByName("CREATOR").ifPresent(role -> todoUserRepository.save(new TodoUser(todo, creator, role)));
        }
    }
//    @CachePut("users")

//    @Transactional
//    public void save(Todo todo) {
//        String title = todo.getTitle();
//        int id = todo.getId();
//        if (todoRepository.existsTodoByTitleAndIdIsNot(title, id)) {
//            throw new TodoNotCreatedException("title <" + title + "> isn't unique");
//        }
//
//        todoRepository.save(todo);
//    }

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
        privilegeRepository.getPrivilegeByName(privilege).ifPresent(role ->
                todoUserRepository.getTodoUserByUserIdAndTodoId(from.getId(), todo.getId()).ifPresent(todoUser -> {
                    Privilege privilege1 = todoUser.getPrivilege();
                    if (Privilege.canSetPrivilege(privilege1, role)) {
                        if (finalPrivilege.equals("NONE")){
                            todoUserRepository.deleteByUserId(to.getId());
                        }
                        else {

                            if(todoRequestRepository.existsByUserIdAndTodoId(to.getId(), todo.getId())){
                                throw new BadCredentialsException("can't create request, bc user has one with this todo");
                            };
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
