package com.example.demo.App.JsonModels;

public class UserVerificationResponse {
    private String info;

    private Boolean exists;

    public UserVerificationResponse() {
    }

    public UserVerificationResponse(String info, boolean exists) {
        this.info = info;
        this.exists = exists;
    }


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    @Override
    public String toString() {
        return "UserVerificationResponse{" +
                "info='" + info + '\'' +
                ", exists=" + exists +
                '}';
    }
}
