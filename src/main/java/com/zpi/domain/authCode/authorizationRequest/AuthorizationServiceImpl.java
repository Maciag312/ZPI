package com.zpi.domain.authCode.authorizationRequest;

import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.authCode.consentRequest.TicketData;
import com.zpi.domain.authCode.consentRequest.TicketRepository;
import com.zpi.domain.common.AuthCodeGenerator;
import com.zpi.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {
    private final TicketRepository repository;
    private final AuthCodeGenerator generator;

    @Override
    public AuthorizationResponse createTicket(User user, AuthenticationRequest request) {
        var ticket = generator.generate();
        var authData = getAuthData(user, request);
        repository.save(ticket, authData);
        return new AuthorizationResponse(ticket, request.getState());
    }

    private TicketData getAuthData(User user, AuthenticationRequest request) {
        var redirectUri = request.getRedirectUri();
        var scope = request.getScope();
        var username = user.getLogin();
        return new TicketData(redirectUri, scope, username);
    }
}
