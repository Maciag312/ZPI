package com.zpi.domain.token.consentRequest;

import com.zpi.domain.common.RequestError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConsentService {
    private final TicketRepository ticketRepository;

    public AuthCode consent(ConsentRequest request) throws ErrorConsentResponseException {
        var ticket = request.getTicket();

        var authData = ticketRepository.getByKey(ticket);
        if (authData.isPresent()) {
            ticketRepository.remove(ticket);

            var authCode = generateAuthCode();

            return new AuthCode(authCode);
        }

        var error = RequestError.<ConsentErrorType>builder()
                .error(ConsentErrorType.TICKET_EXPIRED)
                .errorDescription("Ticket expired")
                .state(request.getState())
                .build();

        throw new ErrorConsentResponseException(error);
    }

    private String generateAuthCode() {
        return UUID.randomUUID().toString();
    }
}
