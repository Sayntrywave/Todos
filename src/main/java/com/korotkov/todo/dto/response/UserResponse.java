package com.korotkov.todo.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {
    private int id;
    private String login;
    private String role;
    private String color;
    private boolean isInBan;
    //role
}
