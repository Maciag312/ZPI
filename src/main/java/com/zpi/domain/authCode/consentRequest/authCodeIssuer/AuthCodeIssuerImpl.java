package com.zpi.domain.authCode.consentRequest.authCodeIssuer;

import com.zpi.domain.authCode.consentRequest.AuthCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthCodeIssuerImpl implements AuthCodeIssuer {
    private final AuthCodeRepository repository;

    @Override
    public AuthCode issue() {
        var code = new AuthCode(generateAuthCode());
        repository.save(code.getValue(), code);

        return code;
    }

    private String generateAuthCode() {
        return UUID.randomUUID().toString();
    }
}
