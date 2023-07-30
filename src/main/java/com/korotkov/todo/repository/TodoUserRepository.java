package com.korotkov.todo.repository;


import com.korotkov.todo.model.TodoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoUserRepository extends JpaRepository<TodoUser,Integer> {
    List<TodoUser> getTodoUsersByTodoId(int id);
    Optional<TodoUser> getTodoUserByUserIdAndTodoId(int userId,int todoId);
    List<TodoUser> getTodoUsersByUserId(int id);
    List<TodoUser> getTodoUsersByPrivilegeId(int id);

    void deleteByUserId(int id);
}
