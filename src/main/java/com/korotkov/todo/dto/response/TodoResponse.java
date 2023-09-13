package com.korotkov.todo.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TodoResponse {
    private int id;
    private String title;
    private String description;
    private List<UserTodo> users;
    @JsonProperty(value = "isCompleted")
    private boolean isCompleted;
    private String timeSpent;
}
