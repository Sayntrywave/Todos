package com.korotkov.todo.service;


import com.korotkov.todo.model.User;
import com.korotkov.todo.repository.RoleRepository;
import com.korotkov.todo.repository.UserRepository;
import com.korotkov.todo.util.exception.UserNotCreatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RegistrationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    @Autowired
    public RegistrationService(UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void register(User user){
        //todo check unique
        String login = user.getLogin();
        if(repository.existsUserByLogin(login)){
            throw new UserNotCreatedException("login <" + login + "> has already been taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        roleRepository.getRoleByName("USER").ifPresent(user::setRole);
        user.setColor("#808080");
        user.setIsInBan(false);
        repository.save(user);

    }
}
