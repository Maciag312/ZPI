package com.zpi.domain.authCode.consentRequest.authCodePersister;

import com.zpi.domain.authCode.consentRequest.AuthCode;
import com.zpi.domain.authCode.consentRequest.AuthUserData;
import com.zpi.domain.authCode.consentRequest.TicketData;
import com.zpi.domain.common.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthCodePersisterImpl implements AuthCodePersister {
    private final AuthCodeRepository repository;
    private final CodeGenerator generator;

    @Override
    public AuthCode persist(TicketData data) {
        var value = generator.ticketCode();
        var scope = data.getScope();
        var redirectUri = data.getRedirectUri();
        var username = data.getUsername();

        var userData = new AuthUserData(scope, redirectUri, username);
        var code = new AuthCode(value, userData);
        repository.save(code.getValue(), code);

        return code;
    }
}
