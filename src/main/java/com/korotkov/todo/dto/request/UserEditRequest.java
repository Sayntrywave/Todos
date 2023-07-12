package com.korotkov.todo.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserEditRequest {

    private String role;
    private String login;
    private String password;
    private String color;
}
