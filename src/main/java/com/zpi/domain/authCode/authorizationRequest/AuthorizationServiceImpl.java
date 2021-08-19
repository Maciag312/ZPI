package com.zpi.domain.authCode.authorizationRequest;

import com.zpi.domain.authCode.authenticationRequest.Request;
import com.zpi.domain.authCode.consentRequest.TicketData;
import com.zpi.domain.authCode.consentRequest.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {
    private final TicketRepository repository;

    @Override
    public AuthorizationResponse createTicket(Request request) {
        var ticket = generateTicket();
        var authData = getAuthData(request);
        repository.save(ticket, authData);
        return new AuthorizationResponse(ticket, request.getState());
    }

    private static String generateTicket() {
        return UUID.randomUUID().toString();
    }

    private TicketData getAuthData(Request request) {
        return TicketData.builder()
                .redirectUri(request.getRedirectUri())
                .build();
    }
}
