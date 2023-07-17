package com.korotkov.todo.dto.response;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TodoResponse {
    private int id;
    private String title;
    private String description;
//    private String createdBy;
    private UserResponse creator;
    private boolean isCompleted;
    private String timeSpent;

    //login, id, role
}
