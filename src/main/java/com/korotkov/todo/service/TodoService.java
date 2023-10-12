package com.korotkov.todo.service;

import com.github.javafaker.Faker;
import com.korotkov.todo.model.*;
import com.korotkov.todo.repository.PrivilegeRepository;
import com.korotkov.todo.repository.TodoRepository;
import com.korotkov.todo.repository.TodoRequestRepository;
import com.korotkov.todo.repository.TodoUserRepository;
import com.korotkov.todo.util.exception.TodoBadCredentialException;
import com.korotkov.todo.util.exception.TodoNotCreatedException;
import com.korotkov.todo.util.exception.TodoNotFoundException;
import com.korotkov.todo.util.exception.UserHasNoPrivilegeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TodoService {

    private final TodoUserRepository todoUserRepository;
    private final TodoRepository todoRepository;
    private final PrivilegeRepository privilegeRepository;
    private final UserService userService;
    private final TodoRequestRepository todoRequestRepository;
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

    public long getCount(int user_id, String query) {
        if (query != null) {
            return todoUserRepository.countTodoUsersByUserIdAndTodo_TitleIgnoreCaseContains(user_id, query);
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
        List<TodoUser> collect;

        if (query != null) {
            collect = todoUserRepository.
                    getTodoUsersByUserIdAndTodo_TitleIgnoreCaseContains(
                            userId,
                            query,
                            PageRequest.of(page, size, Sort.by(sort).descending()))
                    .get()
                    .collect(Collectors.toList());
        } else {
            collect = todoUserRepository.
                    getTodoUsersByUserId(
                            userId,
                            PageRequest.of(page, size, Sort.by(sort).descending()))
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
    public void createTodosForAllUsers(int count) {
        Todo todo;
        Faker faker = new Faker();
        for (User user : userService.getListOfUsers()) {
            for (int i = 0; i < count; i++) {
                todo = new Todo();
                todo.setTitle(faker.name().name());
                todo.setDescription(faker.friends().quote());
                save(todo, user);
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


    @Transactional
    public void update(Todo todo, int id, User currentUser) {
        int currUserId = currentUser.getId();

        Todo todoById = getById(id);


        if (isAllFieldsNull(todo)) {
            return;
        }


        User creatorTodo = todoById.getCreator();

        Optional<TodoUser> todoUserByUserIdAndTodoId = todoUserRepository.getTodoUserByUserIdAndTodoId(currUserId, id);

        Privilege privilege = todoUserByUserIdAndTodoId.orElseThrow(() -> new TodoBadCredentialException("this user isn't related with this todo")).getPrivilege();

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
            save(todoById, creatorTodo);
        } else {
            throw new UserHasNoPrivilegeException("you don't have privilege of todoAction " + todoAction);
        }
    }


    @Transactional
    public void setUser(Todo todo, String privilege, User from, User to) {
        privilege = privilege.toUpperCase();
        String finalPrivilege = privilege;
        privilegeRepository.getPrivilegeByName(privilege).ifPresent(wantedPrivilege ->
                        todoUserRepository.getTodoUserByUserIdAndTodoId(from.getId(), todo.getId()).ifPresent(todoUser -> {
                            Privilege privilege1 = todoUser.getPrivilege();
                            if (Privilege.canSetPrivilege(privilege1, wantedPrivilege)) {
                                if (finalPrivilege.equals("NONE")) {
                                    todoUserRepository.deleteByUserId(to.getId());
                                } else {

                                    Optional<TodoRequest> todoRequest1 = todoRequestRepository.getTodoRequestByUserIdAndTodoId(to.getId(), todo.getId());
                                    if (todoRequest1.isPresent()) {
                                        TodoRequest entity = todoRequest1.get();
                                        entity.setPrivilege(wantedPrivilege);
                                        todoRequestRepository.save(entity);
                                        return;
//                                        throw new BadCredentialsException("can't create request, bc user has one with this todo");
                                    }
                                    Optional<TodoUser> tu = todoUserRepository.getTodoUserByUserIdAndTodoId(to.getId(), todo.getId());
                                    if (tu.isPresent()) {
                                        TodoUser todoUser2 = tu.get();
                                        todoUser2.setPrivilege(wantedPrivilege);
                                        todoUserRepository.save(todoUser2);
                                        cacheManager.getCache("todos").evict(new SimpleKey());
                                    } else {
                                        TodoRequest todoRequest = new TodoRequest(todo, to, wantedPrivilege, from);
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
    public void delete(User updatedBy, int id) {
        Optional<TodoUser> todoUserByUserIdAndTodoId = todoUserRepository.getTodoUserByUserIdAndTodoId(updatedBy.getId(), id);

        Privilege privilege = todoUserByUserIdAndTodoId.orElseThrow(() -> new TodoBadCredentialException("this user isn't related with this todo")).getPrivilege();


        TodoAction todoAction = TodoAction.DELETE;
        if (Privilege.canEditTodo(privilege, todoAction)) {
            cacheManager.getCache("todos").evict(new SimpleKey());

            todoRepository.delete(getById(id));

        } else {
            throw new UserHasNoPrivilegeException("you don't have privilege of todoAction " + todoAction);
        }
    }

}
