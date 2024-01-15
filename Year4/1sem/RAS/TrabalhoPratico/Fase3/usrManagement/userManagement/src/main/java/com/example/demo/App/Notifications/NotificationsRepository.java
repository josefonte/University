package com.example.demo.App.Notifications;

import com.example.demo.App.User.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface NotificationsRepository extends CrudRepository<Notifications, String> {
    @Transactional
    @Modifying
    @Query("SELECT n.users FROM Notifications n WHERE n.id= :type")
    List<User> getAllUsers(String type);
}
