package com.korotkov.todo.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "todos")
@Data
@NoArgsConstructor
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Column(name = "time_spent")
    private Integer timeSpent;

    @OneToMany(targetEntity = User.class)
    @JoinTable(name = "todos_users",
            joinColumns = @JoinColumn(name = "todo_id"),
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> users = new ArrayList<>();

    public List<User> getUsers() {
        return users;
    }


    public Todo(String title, String description) {

        this.title = title;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Todo{" +
               "title='" + title + '\'' +
               '}';
    }

    public User getCreator() {
        if (users.isEmpty()) {
            return null;
        }
        //by default creator is on the 1st index
        return users.get(0);
    }


}
