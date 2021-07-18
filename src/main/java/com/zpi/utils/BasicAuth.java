package com.zpi.utils;

import com.zpi.user.domain.User;
import lombok.Getter;

import java.util.Base64;

@Getter
public class BasicAuth {
    private final String login;
    private final String password;

    public BasicAuth(String basicAuth) {
        byte[] decoded = Base64.getDecoder().decode(basicAuth);
        String decodedString = new String(decoded);

        this.login = decodedString.split(":")[0];
        this.password = decodedString.split(":")[1];
    }

    public static String encodeFrom(User user) {
        var str = user.getLogin() + ":" + user.getPassword();
        return Base64.getEncoder().encodeToString(str.getBytes());
    }
}
