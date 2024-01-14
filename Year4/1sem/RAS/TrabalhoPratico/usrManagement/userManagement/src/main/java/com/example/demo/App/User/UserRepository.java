package com.example.demo.App.User;

import com.example.demo.App.Notifications.NotificationMessage;
import com.example.demo.App.User.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.email = :newEmail WHERE u.number = :number")
    void updateUserEmailByNumber(String number, String newEmail);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.number = :number")
    void updateUserPasswordByNumber(String number, String newPassword);

    @Transactional
    @Query("SELECT u FROM User u WHERE (u.number = :userInfo AND :info = 'number') OR (u.email = :userInfo AND :info = 'email') OR (u.name = :userInfo AND :info = 'name')")
    List<User> findUserByInfo(String info, String userInfo);

    @Transactional
    @Modifying
    @Query("SELECT message FROM User u INNER JOIN u.notificationMessages message WHERE u.number = :number ORDER BY message.messageDate DESC")
    List<NotificationMessage> getAllMessages(String number);

}
