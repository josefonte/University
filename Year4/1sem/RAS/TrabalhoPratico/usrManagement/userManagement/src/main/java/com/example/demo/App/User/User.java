package com.example.demo.App.User;

import com.example.demo.App.JsonModels.MessageFormat;
import com.example.demo.App.Notifications.NotificationMessage;
import com.example.demo.App.Observer.Observer;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Entity
@Service
public class User implements Observer{

    @Id
    private String number;

    private String name;
    private String email;
    private String password;

    //@OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "USERTYPE",
            joinColumns = { @JoinColumn(name = "number") },
            inverseJoinColumns = { @JoinColumn(name = "type") }
    )
    private Set<UsersType> usersTypes;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "user_message",
            joinColumns = { @JoinColumn(name = "number") },
            inverseJoinColumns = { @JoinColumn(name = "id_message") }
    )
    private Set<NotificationMessage> notificationMessages;

    public User(){
        this.usersTypes = new HashSet<>();
    }

    public User(String name, String number, String email){
        this.name = name;
        this.number = number;
        this.email = email;
        this.usersTypes = new HashSet<>();
        this.notificationMessages = new HashSet<>();
    }

    public User(User user){
        this.name = user.getName();
        this.number = user.getNumber();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.usersTypes = new HashSet<>();
        this.notificationMessages = new HashSet<>();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public boolean removeUserType(String type) {
        return this.usersTypes.removeIf(usersType -> usersType.getType().equals(type));
    }

    public void setUserType(UsersType usersType) {
        this.usersTypes.add(usersType);
    }

    @Override
    public void update(Object o){
        MessageFormat messageFormat = (MessageFormat) o;
        this.notificationMessages.add(new NotificationMessage(messageFormat.getMessageBody(), messageFormat.getSubject(), messageFormat.getFrom()));
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public User clone(){
        return new User(this);
    }
}
