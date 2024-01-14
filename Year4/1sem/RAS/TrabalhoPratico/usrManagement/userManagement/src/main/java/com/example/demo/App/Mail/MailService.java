package com.example.demo.App.Mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender mailSender;

    private final String probUMMail;

    @Autowired
    public MailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
        this.probUMMail = "probUM@example.com";
    }


    public void sendEmail(String from, String to, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        this.mailSender.send(message);
    }

    public void sendAccountCreationMail(String to, String number, String password){
        String subject = "ProbUM Account Creation";
        String body = "Welcome aboard to ProbUM!\n" +
                "Your accounts details are:\n\n" +
                "Username Number: " + number + "\n" +
                "Password: " + password + "\n\n" +
                "For security reasons, we recommend changing the password!\n\n" +
                "Best regards,\nThe ProbUM Team";

        sendEmail("ProbUm", to, subject, body);
    }
}
