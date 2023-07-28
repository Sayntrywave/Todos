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

//    @OneToMany
//    @JoinTable(name = "todos_users",
//            joinColumns = @JoinColumn(name = "todo_id"),
//            inverseJoinColumns = @JoinColumn(name = "role_id"))

//    @ElementCollection
//    @CollectionTable(name = "todos_users",
//    joinColumns = @JoinColumn(name = "todo_id"))
////    @JoinColumn(name = "")
//    @MapKeyColumn(name = "user_id")
//    @Column(name = "role_id")
//    @OneToMany
//    @MapKeyJoinColumn(name = "")
//    @Column(name = "role_id")
//    @MapKey(name = "user")
//    @JoinColumn(name = "role_id")
//    @MapKeyJoinColumn(name = "")
//    @MapKeyJoinColumn(name = "user_id")
//    @JoinColumn(name = "role_id")
    @OneToMany(targetEntity = User.class)
    @JoinTable(name = "todos_users",
                joinColumns = @JoinColumn(name = "todo_id"),
                inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> users = new ArrayList<>();

    //todo разобраться как работает jointable под копотом
//    private Map<User,Role> users = new LinkedHashMap<>(); <--- хотел бы так



    //    private Map<User,TodoUser> users = new LinkedHashMap<>();
//    @ElementCollection
//    @CollectionTable(name = "todos_users",
//            joinColumns = @JoinColumn(name = "todo_id"))
//    @MapKeyColumn(name = "user_id")
//    @Column(name = "role_id")
//    private Map<User,Role> rolesByUser = new LinkedHashMap<>();



//    private List<TodoUser> users = new ArrayList<>();
//    @MapKey(name = "User")



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

    public void setUser(){

    }


    public User getCreator() {
        if (users.isEmpty()){
            return null;
        }
//        return users.get(0).getUser();
        //by default creator is on the 1st index
        return users.get(0);
    }

//    public Role getRoleByUser(User user) {
//        return users.get(user).getRole();
//    }

}
