package com.zpi.domain.common;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.UUID;

@Component
public class CodeGeneratorImpl implements CodeGenerator {
    private static final SecureRandom random = new SecureRandom();

    public String ticketCode() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String twoFactorCode() {
        int result = random.nextInt(1_000_000);
        return String.format("%06d%n", result).trim();
    }
}
