package com.korotkov.todo.dto.request;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistrationRequest {
    @NotEmpty
    private String name;
    @NotEmpty
    private String login;
    @NotEmpty
    private String password;

}
