package com.korotkov.todo.service;

import com.korotkov.todo.model.Role;
import com.korotkov.todo.model.Todo;
import com.korotkov.todo.model.User;
import com.korotkov.todo.repository.UserRepository;
import com.korotkov.todo.security.MyUserDetails;
import com.korotkov.todo.util.UserNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    public List<User> getListOfUsers() {

        return userRepository.findAll();
    }
    @Transactional
    public void save(User user){
        userRepository.save(user);
    }
    @Transactional
    public void update(User user, int id, User currentUser){
//        userRepository.

        if(currentUser.getRole().equals(Role.ROLE_USER)){
            return;
        }

        User userToBeUpdated = getById(id);
        userToBeUpdated.setRole(user.getRole());

        int currUserId = currentUser.getId();
//
//        Todo todoById = getById(id);
//
//        if(todoById.getTitle().equals(todo.getTitle()) == todoById.getDescription().equals(todo.getDescription())){
//            return;
//        }
//
//        User creatorTodo = todoById.getCreatedBy(); // in db by id
//
//        if(currentUser.getRole().equals("ROLE_ADMIN") || creatorTodo.getId() == currUserId){
//            todo.setId(id);
//            todo.setCreatedBy(creatorTodo);
//            save(todo); //save
//        }
    }


    public User getById(int id) {
        if (userRepository.existsById(id)) {
            return userRepository.getReferenceById(id);
        }
        throw new UserNotFoundException();
    }


    public User findByLogin(String login){
        return userRepository.findUserByLogin(login).orElseThrow(UserNotFoundException::new);
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

    public List<Todo> getTodos(User user) {
        User user1 = getById(user.getId());
        List<Todo> todos = user1.getTodos();
        Hibernate.initialize(todos);
        return todos;
    }


}
