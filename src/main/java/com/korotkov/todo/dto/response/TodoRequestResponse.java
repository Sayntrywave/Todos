package com.korotkov.todo.dto.response;

import com.korotkov.todo.model.TodoRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

//lol, look at this naming  :)))
@Data
@NoArgsConstructor
public class TodoRequestResponse {

    int todoId;
    String todoTitle;
    String userPrivilege;

    public TodoRequestResponse(TodoRequest request) {
        this.todoId = request.getTodo().getId();
        this.todoTitle = request.getTodo().getTitle();
//        this.userLogin = request.getUser().getLogin();

        this.userPrivilege = request.getPrivilege().getName();
    }
}
