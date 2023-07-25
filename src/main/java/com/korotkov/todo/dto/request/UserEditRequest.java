package com.korotkov.todo.dto.request;

import com.korotkov.todo.model.Role;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserEditRequest {
    private String role;
    @Size(min = 1,max = 30, message = "your login size can't be not in range(1,30)")
    private String login;
    @Size(min = 1,max = 30, message = "your password size can't be not in range(3,30)")
    private String password;
    //todo add validation for color
    private String color;

    public void setRole(Role role) {
        this.role = role.getName();
    }

    public Role getRole() {
        return null;
    }

    public String getStringRole(){
        return role;
    }

    public boolean isNull(){
        return role == null;
    }
}
