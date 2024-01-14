package com.example.demo.App.JsonModels;

public class LoginFormat {
    private String number;

    private String password;

    public LoginFormat(){}

    public LoginFormat(String number, String password) {
        this.number = number;
        this.password = password;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginFormat{" +
                "number='" + number + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
