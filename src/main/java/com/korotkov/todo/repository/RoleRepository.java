package com.korotkov.todo.repository;

import com.korotkov.todo.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Privilege,Integer> {
    Optional<Privilege> getRoleByName(String name);
}
