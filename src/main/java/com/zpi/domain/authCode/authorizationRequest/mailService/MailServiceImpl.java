package com.zpi.domain.authCode.authorizationRequest.mailService;

import com.zpi.domain.rest.ams.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Properties;

@Component
public class MailServiceImpl implements MailService {
    @Value("${email.address}")
    private String senderAddress;

    @Value("${email.password}")
    private String password;

    @Value("${email.server.host}")
    private String host;

    @Value("${email.server.port}")
    private String port;

    @Override
    public void send(String code, User user) {
        String to = user.getLogin();
        String from = senderAddress;

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        properties.setProperty("mail.smtp.auth", "true");

        var auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderAddress, password);
            }
        };

        Session session = Session.getDefaultInstance(properties, auth);

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("ZPI - Two factor code");
            message.setText("2FA security code: " + code);
            message.addHeader("Date", LocalDateTime.now().toString());

            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
            System.out.println(mex.getMessage());
            System.out.println("2FA_CODE: " + code);
        }
    }
}
