package com.korotkov.todo.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
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

    @ManyToOne
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Role role;

    @OneToMany
    @JoinTable(name = "todos_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "todo_id"))
    private List<TodoUser> todos;


    @Column(name = "color")
    private String color;

    @Column(name = "is_in_ban", columnDefinition = "boolean default false")
    private Boolean isInBan;


    public User(String name, String login, String password, Role role) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public User(String login, String password, Role role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public void makeBan() {
        isInBan = !isInBan;
    }


    //    public void setPrivilege(String role) {
//        this.role = new Privilege(role);
//    }
    public void setRole(Role role) {
        this.role = role;
    }


    public String getRole() {
        return role.getName();
    }

    public Role getRoleAsEntity() {
        return role;
    }


    public boolean hasRightsToChange() {
        return role.getName().equals("ADMIN");
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
