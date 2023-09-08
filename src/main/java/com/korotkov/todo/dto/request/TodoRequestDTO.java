package com.korotkov.todo.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class TodoRequestDTO {

    @Size(max = 100,message = "Your can't make title more than 30 symbols. Try shorter :)")
    private String title;
    @Size(max = 450, message = "Your can't make description more than 300 symbols. Try shorter :)")
    private String description;

    private Integer timeSpent;
    //todo add annotation for date
    private Boolean isCompleted;
}
