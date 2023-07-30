package com.korotkov.todo.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "privileges")
@Data
@NoArgsConstructor
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "privilege")
    private String name;

    public Privilege(String name) {
        this.name = name;
    }

    public static boolean canSetPrivilege(Privilege role, Privilege toRole) {
        String roleInString = role.getName().toUpperCase();
        return toRole.getId() > role.getId() && (roleInString.equals("CREATOR") || roleInString.equals("OWNER"));
    }
    public static boolean canEditTodo(Privilege role, TodoAction todoAction) {
        String roleInString = role.getName().toUpperCase();
        boolean b = roleInString.equals("CREATOR") || roleInString.equals("OWNER");
        if (todoAction.equals(TodoAction.EDIT)){
            return b || roleInString.equals("MODERATOR");
        }
        return b;
    }
}