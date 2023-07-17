package com.korotkov.todo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class TodoRequest {
    @NotEmpty
    @Size(max = 30,message = "Your can't make title more than 30 symbols. Try shorter :)")
    private String title;
    @Size(max = 300, message = "Your can't make description more than 300 symbols. Try shorter :)")
    private String description;

    private Integer timeSpent;
    //todo add annotation for date
    private Boolean isCompleted;
}
