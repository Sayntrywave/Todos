package com.korotkov.todo.repository;

import com.korotkov.todo.model.Privilege;
import com.korotkov.todo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<Privilege,Integer> {
    Optional<Privilege> getPrivilegeByName(String name);
}
