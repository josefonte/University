package com.example.demo.App.Notifications;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationsMessageRepository extends CrudRepository<NotificationMessage, Integer> {

}
