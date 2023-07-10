package com.korotkov.todo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class TodoRequest {
    @NotEmpty
    private String title;
//    @NotEmpty
    private String description;
}
