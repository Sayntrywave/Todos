package com.korotkov.todo.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistrationRequest {
    @NotEmpty
    private String name;
    @Size(min = 1,max = 30, message = "your login size should be in range(1,30)")
    private String login;
    @Size(min = 3,max = 30, message = "your password size should be in range(3,30)")
    private String password;

}
