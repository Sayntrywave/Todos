package com.korotkov.todo.service;

import com.korotkov.todo.model.*;
import com.korotkov.todo.repository.RoleRepository;
import com.korotkov.todo.repository.UserRepository;
import com.korotkov.todo.security.MyUserDetails;
import com.korotkov.todo.util.UserHasNoRightsException;
import com.korotkov.todo.util.UserNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
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

    private final RoleRepository roleRepository;
    private CacheManager cacheManager;
    //roleRepository
    //

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.cacheManager = cacheManager;
    }


    @Cacheable("users")
    public List<User> getListOfUsers() {

        return userRepository.findAll();
    }

    @Transactional
    public void save(User user)
    {
        userRepository.save(user);
        cacheManager.getCache("users").evict(new SimpleKey());


    }
    @Transactional
    public boolean update(User user, int id, User currentUser){

        boolean flag = false;
        User userToBeUpdated = getById(id);
        String color = user.getColor();


        Boolean isInBan = user.getIsInBan();
        Privilege roleAsEntity = user.getRoleAsEntity();
        if(color != null && !color.isEmpty() || roleAsEntity != null || isInBan != null){
            //todo check roles
            if(!currentUser.hasRightsToChange()){
                throw new UserHasNoRightsException("you can't change role with role " + currentUser.getRole());
            }


//            Privilege roleByName = roleRepository.getRoleByName(name.substring(5))
//                    .orElseThrow(() -> new UserHasNoRightsException("you can't change role with role " + currentUser.getPrivilege()));
////            User user = userRepository.getReferenceById(id);
//            user.setPrivilege(roleByName);

//            if(role != null && !role.isEmpty()){
//                userToBeUpdated.setPrivilege(role);
//            }
            if(roleAsEntity != null){
                userToBeUpdated.setRole(roleAsEntity);
            }
            if(isInBan != null){
                userToBeUpdated.setIsInBan(isInBan);
            }

            if(color != null){
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

//    @Transactional
//    public void updateRole(String name, int id, User currentUser){
//        if(currentUser.hasRightsToChange()){
//            Privilege roleByName = roleRepository.getRoleByName(name.substring(5))
//                    .orElseThrow(() -> new UserHasNoRightsException("you can't change role with role " + currentUser.getPrivilege()));
////            User user = userRepository.getReferenceById(id);
//            user.setPrivilege(roleByName);
////            save(user);
//        }
//    }
//    public void updateRole(String name, UserTobe id, User currentUser){
//        if(currentUser.hasRightsToChange()){
//            Privilege roleByName = roleRepository.getRoleByName(name.substring(5))
//                    .orElseThrow(() -> new UserHasNoRightsException("you can't change role with role " + currentUser.getPrivilege()));
////            User user = userRepository.getReferenceById(id);
//            user.setPrivilege(roleByName);
////            save(user);
//        }
//    }
//    @Transactional
//    public void makeBannedById(int id, User byUser){
//        if(byUser.isInBan()){
//            throw new UserNotCreatedException("stay in ban loser");
//        }
//        String role = byUser.getPrivilege();
//        if (!role.equals("ROLE_ADMIN")){
//            throw new UserHasNoRightsException("you can't change role with role " + role);
//        }
//
//        if(byUser.getId() == id){
//            throw new UserNotCreatedException("you can't ban yourself");
//        }
//        User userToBeUpdated = getById(id);
//        userToBeUpdated.makeBan();
//        save(userToBeUpdated);
//    }


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

//    @Cacheable(value = "me",key = "")
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


    public UserDetails loadUserByUsername(String username) {
        return null;
    }
}
