package com.korotkov.todo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserEditRequest {
    //todo add validation for role
    private String role;
    @Size(min = 1,max = 30, message = "your login size can't be not in range(1,30)")
    private String login;
    @Size(min = 1,max = 30, message = "your password size can't be not in range(3,30)")
    private String password;
    //todo add validation for color
    private String color;
}
