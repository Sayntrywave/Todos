package com.korotkov.todo.dto.request;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequest {
    @Valid
    private String role;
}
