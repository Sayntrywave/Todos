package com.korotkov.todo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {
    private int id;
    private String login;
    private String role;
    @JsonProperty(value = "isInBan")
    private boolean isInBan;
}
