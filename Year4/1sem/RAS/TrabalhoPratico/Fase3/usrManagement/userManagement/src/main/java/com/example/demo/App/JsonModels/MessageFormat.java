package com.example.demo.App.JsonModels;

public class MessageFormat {

    private String subject;

    private String from;

    private String messageBody;

    public MessageFormat(){}

    public MessageFormat(String subject, String from, String messageBody) {
        this.subject = subject;
        this.from = from;
        this.messageBody = messageBody;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "MessageFormat{" +
                "subject='" + subject + '\'' +
                ", from='" + from + '\'' +
                ", messageBody='" + messageBody + '\'' +
                '}';
    }
}
