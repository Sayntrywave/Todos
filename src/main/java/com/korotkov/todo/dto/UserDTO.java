package com.korotkov.todo.dto;


public class UserDTO {
    private String name;

    public UserDTO(String name) {
        this.name = name;
    }

    public UserDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
