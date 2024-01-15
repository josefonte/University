package com.example.demo.App.Notifications;

import com.example.demo.App.JsonModels.MessageFormat;
import com.example.demo.App.Mail.MailService;
import com.example.demo.App.User.User;
import com.example.demo.App.User.UserRepository;
import com.example.demo.App.User.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NotificationsService {

    private final NotificationsRepository notificationsRepository;

    private final NotificationsMessageRepository notificationsMessageRepository;

    private final UserRepository userRepository;

    private final UserService userService;


    private final MailService mailService;

    @Autowired
    public NotificationsService(NotificationsRepository notificationsRepository,
                                NotificationsMessageRepository notificationsMessageRepository,
                                UserService userService, UserRepository userRepository,
                                MailService mailService){

        this.notificationsRepository = notificationsRepository;
        this.notificationsMessageRepository = notificationsMessageRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }



    @Transactional
    public boolean addNotification(String type, MessageFormat message){
        Optional<Notifications> notificationsOptional = this.notificationsRepository.findById(type);
        if(notificationsOptional.isEmpty()) return false;

        Notifications notification = notificationsOptional.get();
        mailMessage(type, message);
        notification.notifyObservers(message);

        return true;
    }

    @Transactional
    public boolean addNotificationType(String type, List<String> users){
        Optional<Notifications> notificationsOptional = this.notificationsRepository.findById(type);
        if(notificationsOptional.isPresent() || !this.userService.isEveryUserValid(users)) return false;

        Notifications notification = new Notifications(type);

        for(String userNumber: users){
            notification.registerObserver(this.userRepository.findById(userNumber).get());
        }

        this.notificationsRepository.save(notification);
        return true;
    }

    private void mailMessage(String type, MessageFormat messageFormat){
        for(User user: this.notificationsRepository.getAllUsers(type)){
            this.mailService.sendEmail(messageFormat.getFrom(), user.getEmail(), messageFormat.getSubject(), messageFormat.getMessageBody());
        }

    }

    @Transactional
    public boolean removeNotificationType(String type) {
        Optional<Notifications> notificationsOptional = this.notificationsRepository.findById(type);
        if (notificationsOptional.isEmpty()) return false;

        Notifications notification = notificationsOptional.get();
        Set<User> users = new HashSet<>(notification.getUsers());

        for (User user : users) {
            notification.removeObserver(user);
        }

        this.notificationsRepository.delete(notification);
        return true;
    }

    @Transactional
    public List<MessageFormat> getNotificationMessages(String number){
        List<NotificationMessage> notificationMessages = this.userRepository.getAllMessages(number);
        if(notificationMessages.isEmpty()) return null;

        List<MessageFormat> messageFormats = new ArrayList<>();
        for(NotificationMessage notificationMessage: notificationMessages){
            messageFormats.add(new MessageFormat(notificationMessage.getSubject(), notificationMessage.getSender(), notificationMessage.getMessage()));
        }

        return messageFormats;
    }
}
