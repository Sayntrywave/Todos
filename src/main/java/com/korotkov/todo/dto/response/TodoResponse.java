package com.korotkov.todo.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.korotkov.todo.model.Todo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class TodoResponse {
    private int id;
    private String title;
    private String description;
//    private String createdBy;
    private List<UserTodo> users;
    @JsonProperty(value = "isCompleted")
    private boolean isCompleted;
    private String timeSpent;


    //login, id, role
}
