package com.korotkov.todo.dto.request;


import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class TodoUserRequest {
    private String login;
    private String privilege;
}
