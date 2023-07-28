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

    public static boolean canModerate(Role role, Role toRole) {
        String roleInString = role.getName().toUpperCase();
        return toRole.getId() > role.getId() && (roleInString.equals("CREATOR") || roleInString.equals("OWNER"));
    }
}
