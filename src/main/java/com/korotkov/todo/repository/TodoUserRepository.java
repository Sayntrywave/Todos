package com.korotkov.todo.repository;


import com.korotkov.todo.model.TodoUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoUserRepository extends JpaRepository<TodoUser, Integer> {
    List<TodoUser> getTodoUsersByTodoId(int id);

    Optional<TodoUser> getTodoUserByUserIdAndTodoId(int userId, int todoId);

    List<TodoUser> getTodoUsersByPrivilegeId(int id);

    Page<TodoUser> getTodoUsersByUserId(int id, Pageable pageable);

    Page<TodoUser> getTodoUsersByUserIdAndTodo_TitleIgnoreCaseContains(int id, String title, Pageable pageable);


    boolean existsByUserIdAndTodoId(int userId, int todoId);

    long countTodoUsersByUserId(int id);

    long countTodoUsersByUserIdAndTodo_TitleIgnoreCaseContains(int id, String title);


    void deleteByUserId(int id);
}
