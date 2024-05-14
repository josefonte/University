package com.example.braguia2.model.User;



import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.braguia2.model.Trails.Ponto;
import com.example.braguia2.model.Trails.Trail;

import java.util.ArrayList;
import java.util.List;


/* API return
* "last_login": null,
    "is_superuser": false,
    "username": "",
    "first_name": "",
    "last_name": "",
    "email": "",
    "is_staff": false,
    "is_active": false,
    "date_joined": null,
    "groups": [],
    "user_permissions": []
*
* */

@Entity(tableName = "user")
@TypeConverters({IntegerListConverter.class})
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "username")
    String username;

    @ColumnInfo(name = "first_name")
    String first_name;

    @ColumnInfo(name = "last_name")
    String last_name;

    @ColumnInfo(name = "user_type")
    String user_type;

    @ColumnInfo(name = "email")
    String email;

    @ColumnInfo(name = "is_active")
    Boolean is_active;

    @ColumnInfo(name = "is_staff")
    Boolean is_staff;

    @ColumnInfo(name = "is_superuser")
    Boolean is_superuser;


    List<Integer> historico_P = new ArrayList<>();

    List<Integer> historico_T = new ArrayList<>();
    
    public User() {}
    
    public User(@NonNull String username, String user_type) {
        this.username=username;
        this.user_type=user_type;
        this.historico_P = new ArrayList<>();
        this.historico_T = new ArrayList<>();
    }
    
    public User(@NonNull String username, String first_name, String last_name,String user_type, String email, Boolean is_staff, Boolean is_active,  Boolean is_superuser) {
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.user_type = user_type;
        this.email = email;
        this.is_staff = is_staff;
        this.is_active = is_active;
        this.is_superuser = is_superuser;
        this.historico_P = new ArrayList<>();
        this.historico_T = new ArrayList<>();
    }

    public User(User u){
        this.username = u.getUsername();
        this.first_name = u.getFirst_name();
        this.last_name = u.getLast_name();
        this.user_type = u.getUser_type();
        this.email = u.getEmail();
        this.is_staff = u.getIs_staff();
        this.is_active = u.getIs_active();
        this.is_superuser = u.getIs_superuser();
        this.historico_P = u.getHistorico_P();
        this.historico_T = u.getHistorico_P();
    }


    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_type() {
        return this.user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

    public Boolean getIs_staff() {
        return is_staff;
    }

    public void setIs_staff(Boolean is_staff) {
        this.is_staff = is_staff;
    }

    public Boolean getIs_superuser(){
        return this.is_superuser;
    }

    public List<Integer> getHistorico_P() {
        return historico_P;
    }

    public List<Integer> getHistorico_T() {
        return historico_T;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", is_staff=" + is_staff +
                ", is_active=" + is_active +
                ", is_superuser=" + is_superuser +
                '}';
    }

}
