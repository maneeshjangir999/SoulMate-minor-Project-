package com.soulmate.Services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendOtp(String toEmail, String otp){

        SimpleMailMessage message =new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verification code");
        message.setText(STR."Dear User,\n\nYour OTP (One-Time Password) for email verification is: \{otp}\n\nPlease enter this code to verify your email address.\n\nIf you did not request this verification, please ignore this email.\n\nBest regards,\nTeam SoulMate");
        javaMailSender.send(message);

    }
    public String generateOtp() {
        Random random = new Random();
        int otp= 100000 + random.nextInt(900000);
        return String.valueOf(otp);

    }
}
