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

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "todo_id", referencedColumnName = "id")
    private Todo todo;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "privilege_id", referencedColumnName = "id")
    private Privilege privilege;

    public TodoUser(Todo todo, User user, Privilege privilege) {
        this.todo = todo;
        this.user = user;
        this.privilege = privilege;
    }
}
