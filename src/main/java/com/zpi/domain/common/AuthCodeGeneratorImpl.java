package com.zpi.domain.common;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthCodeGeneratorImpl implements AuthCodeGenerator {
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
