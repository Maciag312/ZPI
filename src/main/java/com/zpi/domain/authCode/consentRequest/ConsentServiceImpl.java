package com.zpi.domain.authCode.consentRequest;

import com.zpi.domain.common.RequestError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConsentServiceImpl implements ConsentService {
    private final TicketRepository ticketRepository;

    public ConsentResponse consent(ConsentRequest request) throws ErrorConsentResponseException {
        var ticket = request.getTicket();

        var authData = ticketRepository.getByKey(ticket);
        if (authData.isEmpty()) {
            var error = RequestError.<ConsentErrorType>builder()
                    .error(ConsentErrorType.TICKET_EXPIRED)
                    .errorDescription("Ticket expired")
                    .state(request.getState())
                    .build();

            throw new ErrorConsentResponseException(error);
        }

        ticketRepository.remove(ticket);

        var authCode = new AuthCode(generateAuthCode());
        return new ConsentResponse(authCode, request.getState(), authData.get().getRedirectUri());
    }

    private String generateAuthCode() {
        return UUID.randomUUID().toString();
    }
}
