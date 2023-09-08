package com.korotkov.todo.repository;

import com.korotkov.todo.model.TodoRequest;
import com.korotkov.todo.model.TodoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TodoRequestRepository extends JpaRepository<TodoRequest,Integer> {
    Optional<TodoRequest> getTodoRequestByUserIdAndTodoId(int userId, int todoId);
}
