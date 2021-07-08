package com.zpi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class Greeter {

    private final MessageProvider messageProvider;

    String hello() {
        return messageProvider.getMessage();
    }

    static String hello2() {
        return "Welcome2!";
    }

}
