package com.example.demo.App.Notifications;

import com.example.demo.App.JsonModels.MessageFormat;
import com.example.demo.App.Observer.Observer;
import com.example.demo.App.Observer.Subject;
import com.example.demo.App.User.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Notifications implements Subject {
    @Id
    private String id;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "NOTIFICATION_USER",
            joinColumns = { @JoinColumn(name = "id") },
            inverseJoinColumns = { @JoinColumn(name = "number") }
    )
    private Set<User> users;

    //@OneToMany(cascade = {CascadeType.ALL})
    //private Set<NotificationMessage> notificationMessage;

    public Notifications(){ this.users = new HashSet<>(); }

    public Notifications(String id) {
        this.id = id;
        this.users = new HashSet<>();
        //this.notificationMessage = new HashSet<>();
    }

    public Notifications(Notifications notifications){
        this.id = notifications.getId();
        this.users = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<User> getUsers(){
        return this.users;
    }

    @Override
    public void registerObserver(Observer observer){
        this.users.add((User) observer);
    }

    @Override
    public void removeObserver(Observer observer){
        this.users.remove((User) observer);
    }

    @Override
    public void notifyObservers(Object o){
        for(User user: users){
            user.update(o);
        }
    }

    public Notifications clone()
    {
        return new Notifications(this);
    }
}
