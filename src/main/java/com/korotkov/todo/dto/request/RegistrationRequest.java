package com.korotkov.todo.dto.request;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistrationRequest {
    private String name;
    private String login;
    private String password;


}
