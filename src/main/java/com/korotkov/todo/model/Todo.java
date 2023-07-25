package com.korotkov.todo.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

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

//    @ManyToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "id")
//    private User createdBy;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Column(name = "time_spent")
    private Integer timeSpent;

    @OneToMany
    @JoinTable(name = "todos_users",
            joinColumns = @JoinColumn(name = "todo_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<TodoUser> users = new ArrayList<>();
//    @MapKey(name = "User")
//    private Map<User, Role> usersByRole = new LinkedHashMap<>();



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
        if (users.isEmpty()){
            return null;
        }
        return users.get(0).getUser();
//        return users.entrySet().iterator().next().getKey();
    }
}
