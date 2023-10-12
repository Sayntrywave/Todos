package com.korotkov.todo.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "todo_requests")
@Data
@NoArgsConstructor
public class TodoRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "todo_id", referencedColumnName = "id")
    private Todo todo;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "user_by_id", referencedColumnName = "id")
    private User userBy;

    @ManyToOne
    @JoinColumn(name = "privilege_id", referencedColumnName = "id")
    private Privilege privilege;

    public TodoRequest(Todo todo, User user, Privilege privilege, User userBy) {
        this.todo = todo;
        this.user = user;
        this.userBy = userBy;
        this.privilege = privilege;
    }
}
