package com.revpasswordmanager.serviceimpl;

import com.revpasswordmanager.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        message.setFrom("no-reply@revpasswordmanager.com"); // Shows up as sender
        message.setTo(toEmail);
        message.setSubject("RevPasswordManager - Your Verification Code");
        message.setText("Hello,\n\nYour Two-Factor Authentication verification code is: " + code + "\n\nThis code will expire in 5 minutes.\n\nStay Secure,\nRevPasswordManager Team");

        mailSender.send(message);
    }
}