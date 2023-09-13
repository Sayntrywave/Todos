package com.korotkov.todo.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserEditRequest {


    private Boolean isInBan;
    //    @NotEmpty
    private String role = null;
    @Size(min = 1, max = 30, message = "your login size can't be not in range(1,30)")
    private String login = null;
    @Size(min = 3, max = 30, message = "your password size can't be not in range(3,30)")
    private String password = null;

    private String color = null;

}
