package com.korotkov.todo.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;


    @Column(name = "name")
    private String name;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "createdBy")
    private List<Todo> todos;

    @Column(name = "color")
    private String color;

    @Column(name = "is_in_ban")
    private boolean isInBan;


    public User(String name, String login, String password, String role) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public User(String login, String password, String role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public void setTodo(Todo todo){
        if (todos == null) {
            todos = new ArrayList<>();
        }
        todos.add(todo);
    }

    public void makeBan(){
        isInBan = !isInBan;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", color='" + color + '\'' +
                ", isInBan=" + isInBan +
                '}';
    }
}
