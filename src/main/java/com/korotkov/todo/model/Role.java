package com.korotkov.todo.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "role")
    private String name;

    public Role(String name) {
        this.name = name;
    }

    public static boolean canSetRole(Role role, Role toRole) {
        String roleInString = role.getName().toUpperCase();
        return toRole.getId() > role.getId() && (roleInString.equals("CREATOR") || roleInString.equals("OWNER"));
    }
    public static boolean canEditTodo(Role role, RoleAction action) {
        String roleInString = role.getName().toUpperCase();
        boolean b = roleInString.equals("CREATOR") || roleInString.equals("OWNER");
        if (action.equals(RoleAction.EDIT)){
            return b || roleInString.equals("MODERATOR");
        }
        return b;
    }
}
