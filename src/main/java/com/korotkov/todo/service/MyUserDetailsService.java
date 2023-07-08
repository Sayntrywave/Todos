package com.korotkov.todo.service;


import com.korotkov.todo.model.User;
import com.korotkov.todo.repository.UserRepository;
import com.korotkov.todo.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Autowired
    public MyUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("1");
        Optional<User> user = repository.findUserByUsername(username);



        System.out.println("2");

        if (user.isEmpty()){
            throw new UsernameNotFoundException("user not found");
        }

        return new MyUserDetails(user.get());
    }
}
