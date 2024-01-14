package com.example.demo.App.Notifications;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class NotificationMessage {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id_message;


    private String subject;

    private String sender;

    private String message;

    private LocalDateTime messageDate;

    public NotificationMessage(){
    }

    public NotificationMessage(String message, String subject, String sender) {
        this.message = message;
        this.subject = subject;
        this.sender = sender;
        this.messageDate = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String type) {
        this.message = type;
    }

    public int getId_message() {
        return id_message;
    }

    public void setId_message(int id_message) {
        this.id_message = id_message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String from) {
        this.sender = from;
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "id_message=" + id_message +
                ", subject='" + subject + '\'' +
                ", from='" + sender + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
