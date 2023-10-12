package com.korotkov.todo.service;

import com.korotkov.todo.model.Role;
import com.korotkov.todo.model.TodoRequest;
import com.korotkov.todo.model.TodoUser;
import com.korotkov.todo.model.User;
import com.korotkov.todo.repository.TodoRequestRepository;
import com.korotkov.todo.repository.TodoUserRepository;
import com.korotkov.todo.repository.UserRepository;
import com.korotkov.todo.security.MyUserDetails;
import com.korotkov.todo.util.exception.UserHasNoRightsException;
import com.korotkov.todo.util.exception.UserNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final TodoRequestRepository todoRequestRepository;

    private final TodoUserRepository todoUserRepository;
    private final CacheManager cacheManager;

    @Autowired
    public UserService(UserRepository userRepository,

                       PasswordEncoder passwordEncoder,
                       TodoRequestRepository todoRequestRepository,
                       TodoUserRepository todoUserRepository,
                       CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.todoRequestRepository = todoRequestRepository;
        this.todoUserRepository = todoUserRepository;
        this.cacheManager = cacheManager;
    }


    //    @Cacheable("users")
    public List<User> getListOfUsers() {

        return userRepository.findAll();
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
        cacheManager.getCache("users").evict(new SimpleKey());


    }

    @Transactional
    public boolean update(User user, int id, User currentUser) {

        boolean flag = false;
        User userToBeUpdated = getById(id);
        String color = user.getColor();


        Boolean isInBan = user.getIsInBan();
        Role roleAsEntity = user.getRoleAsEntity();
        if (color != null && !color.isEmpty() || roleAsEntity != null || isInBan != null) {
            if (!currentUser.hasRightsToChange()) {
                throw new UserHasNoRightsException("you can't change role with role " + currentUser.getRole());
            }

            if (roleAsEntity != null) {
                userToBeUpdated.setRole(roleAsEntity);
            }
            if (isInBan != null) {
                userToBeUpdated.setIsInBan(isInBan);
            }

            if (color != null) {
                userToBeUpdated.setColor(color);

            }
        }
        String login = user.getLogin();
        String password = user.getPassword();
        if (login != null || password != null) {
            if (currentUser.getId() != id) {
                throw new UserHasNoRightsException("you can't change another user info");
            }
            if (login != null && !login.isEmpty()) {

                if (userRepository.existsUserByLogin(login)) {
                    throw new BadCredentialsException("login <" + login + "> has already been taken");
                }
                flag = true;
                userToBeUpdated.setLogin(login);
            }
            if (password != null && !password.isEmpty()) {
                userToBeUpdated.setPassword(passwordEncoder.encode(password));
            }

        }
        save(userToBeUpdated);
        return flag;
    }

    @Transactional
    public void acceptTodoRequest(int todo_id, User user, Boolean accepted) {
        TodoRequest optTU = todoRequestRepository.getTodoRequestByUserIdAndTodoId(user.getId(), todo_id).orElseThrow();
        if (accepted) {
            todoUserRepository.save(new TodoUser(optTU.getTodo(),
                    optTU.getUser(),
                    optTU.getPrivilege()));
            cacheManager.getCache("todos").evict(new SimpleKey());
        }
        todoRequestRepository.delete(optTU);

    }

    public List<TodoRequest> getTodoRequests(User user) {
        return todoRequestRepository.getTodoRequestByUserId(user.getId());
    }

    public User getById(int id) {
        if (userRepository.existsById(id)) {
            return userRepository.getReferenceById(id);
        }
        throw new UserNotFoundException("user not found");
    }


    public User findByLogin(String login) {
        return userRepository.findUserByLogin(login).orElseThrow(() -> new UserNotFoundException("user not found"));
    }

    public User getCurrentUser() {
        //todo get user from jwt token
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((MyUserDetails) principal).user();
        }

        //trow exception
        return new User();
    }

    public List<TodoUser> getTodos(User user) {
        User user1 = getById(user.getId());
        List<TodoUser> todos = user1.getTodos();
        Hibernate.initialize(todos);
        return todos;
    }

}
