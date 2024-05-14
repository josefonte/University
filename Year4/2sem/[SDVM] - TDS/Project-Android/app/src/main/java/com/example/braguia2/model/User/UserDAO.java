package com.example.braguia2.model.User;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Delete
    void delete(User user);

   @Update
    int update(User user);


    @Query("SELECT DISTINCT * FROM user")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM user WHERE username = :username")
    LiveData<User> getUser(String username);

    @Query("DELETE FROM user")
    void deleteAll();

    default void insertOrUpdate(User u) {
        User user = new User(u);
        if(!(update(user)>0)){
            insert(user);
        }
    }
}
