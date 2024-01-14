package com.example.demo.App.JsonModels;

public class UserInfoRequest {

    private String name;

    private String number;
    private String email;

    private String password;

    public UserInfoRequest(){}

    public UserInfoRequest(String name, String number, String email, String password) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

