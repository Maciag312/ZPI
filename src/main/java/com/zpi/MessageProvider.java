package com.zpi;

import org.springframework.stereotype.Component;

@Component
public class MessageProvider {

    private String message = "some message";

    public String getMessage() {
        return message;
    }
}
