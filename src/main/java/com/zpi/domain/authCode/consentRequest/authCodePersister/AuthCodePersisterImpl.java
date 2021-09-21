package com.zpi.domain.authCode.consentRequest.authCodePersister;

import com.zpi.domain.authCode.consentRequest.AuthCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthCodePersisterImpl implements AuthCodePersister {
    private final AuthCodeRepository repository;

    @Override
    public AuthCode persist(String scope) {
        var code = new AuthCode(generateAuthCode(), scope);
        repository.save(code.getValue(), code);

        return code;
    }

    private String generateAuthCode() {
        return UUID.randomUUID().toString();
    }
}
