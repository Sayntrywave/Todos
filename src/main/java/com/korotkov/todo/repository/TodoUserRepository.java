package com.korotkov.todo.repository;


import com.korotkov.todo.model.TodoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoUserRepository extends JpaRepository<TodoUser,Integer> {

}
