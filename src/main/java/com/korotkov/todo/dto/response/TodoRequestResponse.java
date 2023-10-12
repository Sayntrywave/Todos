package com.korotkov.todo.dto.response;

import com.korotkov.todo.model.TodoRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

//lol, look at this naming  :)))
@Data
@NoArgsConstructor
public class TodoRequestResponse {

    private String userLogin;
    private int todoId;
    private String todoTitle;
    private String userPrivilege;

    public TodoRequestResponse(TodoRequest request) {
        this.todoId = request.getTodo().getId();
        this.todoTitle = request.getTodo().getTitle();
        this.userLogin = request.getUserBy().getLogin();

        this.userPrivilege = request.getPrivilege().getName();
    }
}
