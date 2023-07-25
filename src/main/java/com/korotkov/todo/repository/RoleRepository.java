package com.korotkov.todo.repository;

import com.korotkov.todo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> getRoleByName(String name);
}
