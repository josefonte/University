package com.example.demo.App.Notifications;

import com.example.demo.App.JsonModels.MessageFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(path = "api/usr")
public class NotificationsController {

    private final NotificationsService notificationsService;

    @Autowired
    public NotificationsController(NotificationsService notificationsService){
        this.notificationsService = notificationsService;
    }

    @GetMapping(path="/notifications")
    public ResponseEntity<Object> getNotificationType(@RequestParam String number){
        List<MessageFormat> notificationMessages = this.notificationsService.getNotificationMessages(number);
        return  notificationMessages!=null
                ? ResponseEntity.ok(notificationMessages)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No messages found");
    }

    @PutMapping(path="/notifications")
    public ResponseEntity<String> addNotification(@RequestParam String type, @RequestBody MessageFormat message){
        return this.notificationsService.addNotification(type, message)
                ? ResponseEntity.ok("Notification added")
                : ResponseEntity.badRequest().body("The exam associated is not valid");
    }

    @PostMapping(path="/notifications")
    public ResponseEntity<String> createNotificationType(@RequestParam String type, @RequestBody List<String> users){
        return this.notificationsService.addNotificationType(type, users)
                ? ResponseEntity.ok("Notification type created")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Parameters");
    }

    @DeleteMapping(path="/notifications")
    public ResponseEntity<String> deleteNotificationType(@RequestParam String type){
        return this.notificationsService.removeNotificationType(type)
                ? ResponseEntity.ok("Notification type removed")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Notification Type");
    }
}
