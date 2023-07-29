package com.korotkov.todo.dto.response;


import com.korotkov.todo.model.Role;
import com.korotkov.todo.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTodo {
    private UserResponse user;
    private String privilege;
}
