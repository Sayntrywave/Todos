package com.korotkov.todo.service;

import com.korotkov.todo.model.Todo;
import com.korotkov.todo.model.User;
import com.korotkov.todo.repository.UserRepository;
import com.korotkov.todo.security.MyUserDetails;
import com.korotkov.todo.util.UserHasNoRightsException;
import com.korotkov.todo.util.UserNotCreatedException;
import com.korotkov.todo.util.UserNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }



    public List<User> getListOfUsers() {

        return userRepository.findAll();
    }
    @Transactional
    public void save(User user){
        userRepository.save(user);
    }
    @Transactional
    public boolean update(User user, int id, User currentUser){

        boolean flag = false;
        User userToBeUpdated = getById(id);
        String role = user.getRole();
        String color = user.getColor();
        if((role != null && !role.isEmpty())|| (color != null && !color.isEmpty())){
            //todo check roles
            if(!currentUser.getRole().equals("ROLE_ADMIN")){
                throw new UserHasNoRightsException("you can't change role with role " + currentUser.getRole());
            }

            if(role != null && !role.isEmpty()){
                userToBeUpdated.setRole(role);
            }
            if(color != null && !color.isEmpty()){
                userToBeUpdated.setColor(color);
            }
        }
        String login = user.getLogin();
        String password = user.getPassword();
        if(login != null || password != null) {
            if(currentUser.getId() != id){
                throw new UserHasNoRightsException("you can't change another user info");
            }
            if(login != null && !login.isEmpty()){

                if(userRepository.existsUserByLogin(login)){
                    throw new BadCredentialsException("login <" + login + "> has already been taken");
                }
                flag = true;
                userToBeUpdated.setLogin(login);
            }
            if(password != null && !password.isEmpty()){
                userToBeUpdated.setPassword(passwordEncoder.encode(password));
            }

        }
        save(userToBeUpdated);
        return flag;
    }
    @Transactional
    public void makeBannedById(int id, User byUser){
        if(byUser.isInBan()){
            throw new UserNotCreatedException("stay in ban loser");
        }
        String role = byUser.getRole();
        if (!role.equals("ROLE_ADMIN")){
            throw new UserHasNoRightsException("you can't change role with role " + role);
        }

        if(byUser.getId() == id){
            throw new UserNotCreatedException("you can't ban yourself");
        }
        User userToBeUpdated = getById(id);
        userToBeUpdated.makeBan();
        save(userToBeUpdated);
    }


    public User getById(int id) {
        //переделать
        if (userRepository.existsById(id)) {
            return userRepository.getReferenceById(id);
        }
        throw new UserNotFoundException("user not found");
    }


    public User findByLogin(String login){
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

    public List<Todo> getTodos(User user) {
        User user1 = getById(user.getId());
        List<Todo> todos = user1.getTodos();
        Hibernate.initialize(todos);
        return todos;
    }


    public UserDetails loadUserByUsername(String username) {
        return null;
    }
}
