package com.korotkov.todo.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "todos_users")
@Data
@NoArgsConstructor
public class TodoUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "todo_id",referencedColumnName = "id")
    private Todo todo;
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id",referencedColumnName = "id")
    private Role role;

    public TodoUser(Todo todo, User user, Role role) {
        this.todo = todo;
        this.user = user;
        this.role = role;
    }
}
