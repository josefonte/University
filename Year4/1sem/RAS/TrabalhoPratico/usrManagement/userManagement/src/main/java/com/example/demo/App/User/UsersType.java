package com.example.demo.App.User;

import com.example.demo.App.User.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class UsersType {
    @Id
    private String type;

    //@ManyToOne
    //@JoinColumn(name="number", nullable = false)
    //private User user;

    public UsersType(){

    }

    public UsersType(User user, String type){
        this.type = type;
    }

    public UsersType(UsersType usersType){
        this.type = usersType.getType();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "UserType{" +
                "type='" + type + '\'' +
                '}';
    }
}


