package com.example.demo.App.JsonModels;

public class UserInfoResponse {

    private String name;
    private String email;

    public UserInfoResponse() {
    }

    public UserInfoResponse(String name, String email) {
        this.name = name;
        this.email = email;
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

}
