package com.zpi.infrastructure.mail;

import com.zpi.domain.authCode.authorizationRequest.MailService;
import com.zpi.domain.rest.ams.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    String emailFrom;

    @Override
    public void send(String code, User user) {
        var to = user.getEmail();
        var subject = "ZPI - Two factor code";
        var text = "2FA security code: " + code;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            emailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("2FA_CODE: " + code);
        }
    }
}
